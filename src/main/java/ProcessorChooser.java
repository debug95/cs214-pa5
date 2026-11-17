public final class ProcessorChooser {
    private ProcessorChooser() {
    }

    static void processorHandler(String[] args) {
        if (args.length == 2) {
            PA1Processor.run(args[0], args[1]);
            return;
        }

        if (args.length < 3) {
            throw new IllegalArgumentException("Error: incorrect number of arguments");
        }

        switch (args[2]) {
            case "-a":
                PA2Processor.run(args[0], args[1]);
                break;

            case "-u":
                PA3Processor.run(args[0], args[1]);
                break;

            case "-p":
                PA4Processor.run(args[0], args[1]);
                break;

            case "-r":
                if (args.length < 4) {
                    throw new IllegalArgumentException("Error: must select at least one song for recommendations");
                }

                String[] selections = java.util.Arrays.copyOfRange(args, 3, args.length);

                PA5Processor.run(args[0], args[1], selections);
                break;

            default:
                throw new IllegalArgumentException("Error: invalid third argument");
        }
    }
}