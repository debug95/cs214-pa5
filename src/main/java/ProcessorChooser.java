public final class ProcessorChooser {
    private ProcessorChooser() {
    }

    static void processorHandler(String[] args) {
        if (args.length == 2) {
            PA1Processor.run(args[0], args[1]);
            return;
        }

        validateThreeArgs(args);

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

            default:
                throw new IllegalArgumentException("Error: invalid third argument");
        }
    }

    private static void validateThreeArgs(String[] args) {

        if (args.length != 3) {
            throw new IllegalArgumentException("Error: incorrect number of arguments");
        }
    }
}
