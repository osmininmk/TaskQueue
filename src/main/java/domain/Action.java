package domain;

/*
 * Class represented action on Task
 */
public enum Action {
    PLUS_PLUS {
        /*
         *{@inheritDoc}
         */
        @Override
		public int calculate(int a, int b, int c) {
            return a + b + c;
        }
    },
    PLUS_MINUS {
        /*
         *{@inheritDoc}
         */
        @Override
		public int calculate(int a, int b, int c) {
            return a + b - c;
        }
    },
    MINUS_MINUS {
        /*
         *{@inheritDoc}
         */
        @Override
		public int calculate(int a, int b, int c) {
            return a - b - c;
        }
    },
    MINUS_PLUS {
        /*
         *{@inheritDoc}
         */
        @Override
		public int calculate(int a, int b, int c) {
            return a - b + c;
        }
    };
    /*
     * Calculate value from input parameters based on current enum value
     *
     * @param a first value
     * @param b second value
     * @param c third value
     *
     * @return result of mathematical expresson from input param based on current enum
     */
    public abstract int calculate(int a, int b, int c);
}
