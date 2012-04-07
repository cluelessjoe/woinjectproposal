package com.resurgences.utils;

public class AssertUtils {

    public static void assertParametersNotNull(String parameterNames, Object... objects) {
        doAssertNotNull("parameter", parameterNames, objects);
    }

    private static void doAssertNotNull(String elementType, String parameterNames, Object... objects) {
        if (objects == null) {
            objects = new Object[] { null };
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] == null) {
                    AssertUtils.raiseError("The " + elementType + " at the position " + i
                            + " is null in the context of " + parameterNames + ".");
                }
            }
        }
    }

    public static void assertNotNull(String variableNames, Object... objects) {
        doAssertNotNull("variable", variableNames, objects);
    }

    public static final void raiseError(final String error) {
        throw new AssertionFailedException(error);
    }

    public static class AssertionFailedException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public AssertionFailedException() {
        }

        public AssertionFailedException(final String detail) {
            super(detail);
        }

        public AssertionFailedException(final String detail, final Throwable e) {
            super(detail, e);
        }
    }
}
