package net.silthus.schat.util;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public final class UUIDUtil {

    private static final Predicate<String> UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}", CASE_INSENSITIVE).asMatchPredicate();

    public static boolean isUuid(String id) {
        return UUID_PATTERN.test(id);
    }

    private UUIDUtil() {
    }
}
