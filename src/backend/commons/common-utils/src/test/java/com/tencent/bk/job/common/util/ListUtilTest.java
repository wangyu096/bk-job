package com.tencent.bk.job.common.util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ListUtilTest {

    @Test
    void testIsAllTrue() {
        List<Boolean> allTrueList = new ArrayList<>();
        assertThat(ListUtil.isAllTrue(allTrueList)).isTrue();
        allTrueList.add(true);
        allTrueList.add(true);
        allTrueList.add(true);
        assertThat(ListUtil.isAllTrue(allTrueList)).isTrue();
        List<Boolean> notAllTrueList = new ArrayList<>();
        notAllTrueList.add(true);
        notAllTrueList.add(false);
        notAllTrueList.add(false);
        assertThat(ListUtil.isAllTrue(notAllTrueList)).isFalse();
    }

    @Test
    void testSeparate() {
        List<String> list = Arrays.asList("a", "abc", "acc", "abb", "cc", "dd", "ee", null, "ff");
        Pair<List<String>, List<String>> pair = ListUtil.separate(list, s -> s != null && s.contains("a"));
        assertThat(pair.getLeft().size() + pair.getRight().size() == list.size());
        pair.getLeft().forEach(s -> assertThat(s.contains("a")));
        pair.getRight().forEach(s -> assertThat(s == null || !s.contains("a")));
    }

}
