package worker.communication.scheduler;

public class GetLogsCommand {
    private int offset;
    private int limit;

    public GetLogsCommand(int offset, int limit) {
        if (offset < 0) {
            throw new RuntimeException("Offset must be greater or equal 0");
        }

        if (limit < 1) {
            throw new RuntimeException("Offset must be greater or equal 1");
        }

        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
