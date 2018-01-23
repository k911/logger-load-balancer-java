package worker.server.config;

import java.util.List;

public class DaoConfiguration {

    private String usedSchema;
    private List<String> createStatements;

    public DaoConfiguration() {

    }

    public String getUsedSchema() {
        return usedSchema;
    }

    public void setUsedSchema(String usedSchema) {
        this.usedSchema = usedSchema;
    }

    public List<String> getCreateStatements() {
        return createStatements;
    }

    public void setCreateStatements(List<String> createStatements) {
        this.createStatements = createStatements;
    }
}
