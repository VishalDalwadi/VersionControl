package VCS.DataStructures;

public class Node<Datatype> {
    private Datatype data;
    private Node<Datatype> next;

    public Node(Datatype data) {
        this.data = data;
    }

    public void setData(Datatype data) {
        this.data = data;
    }

    public Datatype getData() {
        return data;
    }

    public void setNext(Node<Datatype> next) {
        this.next = next;
    }

    public Node<Datatype> getNext() {
        return next;
    }
}
