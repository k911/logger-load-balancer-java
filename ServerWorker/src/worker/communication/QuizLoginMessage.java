package worker.communication;


import java.io.Serializable;


public class QuizLoginMessage extends SocketMessage implements Serializable {

    private static final long serialVersionUID = -7288386688322415847L;

    public QuizLoginMessage(String author) {
        super(author);

    }

    @Deprecated
    public QuizLoginMessage() {

    }
}
