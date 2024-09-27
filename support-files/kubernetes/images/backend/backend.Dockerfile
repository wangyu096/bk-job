FROM bkjob/jdk:0.0.3

LABEL maintainer="Tencent BlueKing Job"

ENV BK_JOB_HOME=/data/job/exec

COPY ./ /data/job/exec/
RUN ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo 'Asia/Shanghai' > /etc/timezone && \
    chmod +x /data/job/exec/startup.sh && \
    chmod +x /data/job/exec/tini

RUN mkdir -p /opt && cd /opt && git clone https://github.com/jemalloc/jemalloc.git \
    && mkdir /tmp/jprof && mkdir /tmp/nmt && mkdir /tmp/pmap \
    && mkdir /diagnostic

RUN cd /opt/jemalloc && git checkout -b stable-4 origin/stable-4
RUN cd /opt/jemalloc && ./autogen.sh --enable-prof
RUN cd /opt/jemalloc && make dist
RUN cd /opt/jemalloc && make
RUN cd /opt/jemalloc && make install

ENV LD_PRELOAD="/usr/local/lib/libjemalloc.so"
ENV MALLOC_CONF="prof_leak:true,prof:true,lg_prof_interval:25,lg_prof_sample:18,prof_prefix:/tmp/jeprof"

ENV DIAGNOSTIC_DIR /diagnostic
RUN mkdir -p "$DIAGNOSTIC_DIR"
COPY *.sh $DIAGNOSTIC_DIR/

ENV LANG en_US.utf8
ENV LANGUAGE en_US.utf8
ENV LC_ALL en_US.utf8

WORKDIR /data/job/exec

ENTRYPOINT ["./tini", "--", "/data/job/exec/startup.sh"]
