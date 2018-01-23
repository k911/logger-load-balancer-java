package ServerLoadBalancer.src.utils;

import com.google.gson.Gson;
import ServerLoadBalancer.src.items.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLogGenerator {

    private ArrayList<String> templateMessages;
    private ArrayList<String> templateGender;
    private ArrayList<String> templateEyes;
    private ArrayList<String> templateRoles;
    private ArrayList<String> templateTypes;
    private long endTimestamp = Timestamp.valueOf("2017-01-01 00:00:00").getTime();
    private long offsetTimestamp = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
    private Gson gson;

    public RandomLogGenerator(Gson gson) {
        this.gson = gson;

        templateMessages = new ArrayList<>();
        templateMessages.add("Bad request");
        templateMessages.add("Success");
        templateMessages.add("RuntimeException");
        templateMessages.add("START TRANSACTION");
        templateMessages.add("ROLLBACK");
        templateMessages.add("COMMIT");
        templateMessages.add("FATAL ERROR");

        templateGender = new ArrayList<>();
        templateGender.add("male");
        templateGender.add("female");
        templateGender.add("undefined");

        templateEyes = new ArrayList<>();
        templateEyes.add("brown");
        templateEyes.add("green");
        templateEyes.add("blue");
        templateEyes.add("black");

        templateRoles = new ArrayList<>();
        templateRoles.add("USER");
        templateRoles.add("ADMIN");
        templateRoles.add("READER");
        templateRoles.add("MODERATOR");
        templateRoles.add("SUPER_ADMIN");
        templateRoles.add("WRITER");
        templateRoles.add("USER_WRITER");

        templateTypes = new ArrayList<>();
        templateTypes.add("WORKER");
        templateTypes.add("HTTP");
        templateTypes.add("SOCKET");
        templateTypes.add("IO");
        templateTypes.add("DATABASE");
    }

    private int getRandomAge() {
        return ThreadLocalRandom.current().nextInt(20, 41);
    }

    private String getRandomString(List<String> list) {
        Collections.shuffle(list);

        return list.get(0);
    }

    private List<String> getRandomCollection(List<String> collection) {
        List<String> randomCollection = new ArrayList<>();
        int count = ThreadLocalRandom.current().nextInt(0, collection.size());
        while (count > 0) {
            randomCollection.add(getRandomString(collection));
            --count;
        }

        return randomCollection;
    }

    private Timestamp getRandomTimestamp() {
        long diff = endTimestamp - offsetTimestamp + 1;
        return new Timestamp(offsetTimestamp + (long) (Math.random() * diff));
    }

    private boolean getRandomBool() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public Log generate() {
        TemplateUser user = new TemplateUser(UUID.randomUUID().toString(), getRandomString(templateGender), getRandomString(templateEyes), getRandomCollection(templateRoles), getRandomBool(), getRandomBool(), getRandomAge());
        TemplateContext context = new TemplateContext(getRandomString(templateTypes), user);

        return new Log(getRandomString(templateMessages), gson.toJson(context), getRandomTimestamp());
    }
}
