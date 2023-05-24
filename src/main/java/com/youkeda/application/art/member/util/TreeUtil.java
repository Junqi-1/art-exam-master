package com.youkeda.application.art.member.util;

import com.youkeda.application.art.member.model.Tree;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TreeUtil {

    private static final Logger log = LoggerFactory.getLogger(TreeUtil.class);

    public static List<Tree> convertTreeType(List<Object> items) {
        final List<Tree> trees = new ArrayList<>();
        items.forEach(i -> trees.add((Tree)i));
        return trees;
    }

    public static <T> List<T> convertToTree(Tree[] items, T defaultParent, BiConsumer<T, T> consumer,
                                            Consumer<List<T>> sort) {

        Tree parentTree = (Tree)defaultParent;

        Map<String, Tree> nodes = new HashMap<>();
        for (Tree item : items) {
            nodes.put(item.getId(), item);
            if (item.getParentId() == null) {
                item.setParentId("0");
            }
        }

        String pid = parentTree.getId();
        if (StringUtils.isEmpty(pid)) {
            pid = "0";
        }
        nodes.put(pid, parentTree);

        for (Tree item : items) {
            String parentId = StringUtils.isEmpty(item.getParentId()) ? "0" : item.getParentId();
            Tree parent = nodes.get(parentId);

            if (parent == null) {
                // 记录错误数据
                log.error("the tree data has no parent {}", item.getId());
                continue;
            }
            parent.addChildren(nodes.get(item.getId()));
        }

        List<T> result = parentTree.getChildren();
        if (sort != null && !CollectionUtils.isEmpty(result)) {
            sort.accept(result);
        }
        eachTree(consumer, sort, result.toArray(new Tree[] {}));
        return result;
    }

    /**
     * 创建树
     *
     * @param items
     * @return
     */
    public static <T> List<T> convertToTree(Tree[] items, T defaultParent, BiConsumer<T, T> consumer) {
        return convertToTree(items, defaultParent, consumer, null);
    }

    public static <T> void eachTree(BiConsumer<T, T> consumer, Consumer<List<T>> sort, Tree... items) {

        for (Tree item : items) {
            eachTree(item, null, consumer, sort);
        }
    }

    /**
     * 遍历 tree，处理想要处理的行为
     *
     * @param items
     * @param consumer
     * @param <T>
     */
    public static <T> void eachTree(BiConsumer<T, T> consumer, Tree... items) {
        eachTree(consumer, null, items);
    }

    private static <T> void eachTree(Tree item, Tree parent, BiConsumer<T, T> consumer, Consumer<List<T>> sort) {
        if (consumer != null) {
            consumer.accept((T)parent, (T)item);
        }
        if (item.getChildren() == null || item.getChildren().isEmpty()) {
            return;
        }
        item.getChildren().forEach(i -> {
            eachTree((Tree)i, item, consumer, sort);
        });
        if (sort != null) {
            sort.accept(item.getChildren());
        }
    }

}
