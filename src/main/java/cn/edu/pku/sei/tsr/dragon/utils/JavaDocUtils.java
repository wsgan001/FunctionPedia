package cn.edu.pku.sei.tsr.dragon.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.dom.TagElement;

public class JavaDocUtils {
    public static Pair<String, String> splitArgAndDescription(TagElement tag) {
        if (tag.getTagName() != null && tag.getTagName().equals("@param")) {
            String description = StringUtils.join(tag.fragments(), "");
            List<String> split = Arrays.asList(description.split(" "));
            return new ImmutablePair<>(split.get(0), StringUtils.join(split.subList(1, split.size()), " "));
        }
        return null;
    }
}
