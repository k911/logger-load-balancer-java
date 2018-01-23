package worker.server.database;


import java.util.List;


public class QueryBuilder {


    private final String select = "SELECT ";
    private final String from = " FROM ";
    private final String where = " WHERE ";
    private final String join = " JOIN ";
    private final String on = " ON ";
    private final String inner = " INNER ";
    private final String left = " LEFT ";
    private final String right = " RIGHT ";
    private final String like = " LIKE ";
    private final String in = " IN ";
    private final String not = " NOT ";
    private final String insert = " INSERT INTO ";
    private StringBuilder stringBuilder;

    private List<String> selectedColumns;
    private List<String> fromTables;

    private String queryString;


    public QueryBuilder() {
        stringBuilder = new StringBuilder(64);
    }

    public QueryBuilder(Integer bufferSize) {
        stringBuilder = new StringBuilder(bufferSize);
    }

    String BuildQuery() {
        stringBuilder.append(";");
        return stringBuilder.toString();
    }


    public QueryBuilder select(List<String> columns) {
        stringBuilder.append(select);
        stringBuilder.append(ListToString(columns));
        return this;
    }


    public QueryBuilder columns(List<String> columns) {
        stringBuilder.append(ListToString(columns));

        return this;
    }

    public QueryBuilder table(String table) {
        stringBuilder.append(table);
        return this;
    }

    public QueryBuilder from(List<String> tables) {
        this.fromTables = tables;
        stringBuilder.append(from);
        stringBuilder.append(ListToString(tables));
        return this;
    }

    public QueryBuilder where(String condition) {
        stringBuilder.append(where);
        stringBuilder.append(condition);
        return this;
    }

    public QueryBuilder join(JoinType type, String table, String condition) {

        String joinTypeString;
        switch (type) {
            case INNER:
                joinTypeString = inner;
                break;
            case LEFT:
                joinTypeString = left;
                break;
            case RIGHT:
                joinTypeString = right;
                break;
            default:
                joinTypeString = "";
                break;
        }


        stringBuilder.append(joinTypeString);
        stringBuilder.append(join);
        stringBuilder.append(table);
        stringBuilder.append(on);
        stringBuilder.append(condition);

        return this;

    }

    public QueryBuilder insert() {
        stringBuilder.append(insert);
        return this;
    }

    public QueryBuilder values(List<String> columns, List<String> values) {
        stringBuilder.append(ListToParenthesesString(columns));
        stringBuilder.append(" VALUES ");
        stringBuilder.append(ListToParenthesesString(values));

        return this;
    }

    private String ListToString(List<String> list) {
        String result = new String();

        return result.join(",", list);
    }

    private String ListToParenthesesString(List<String> list) {

        String result = new String();

        return "(" + result.join(",", list) + ")";
    }

}
