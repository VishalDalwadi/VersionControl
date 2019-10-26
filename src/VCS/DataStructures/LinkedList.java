package VCS.DataStructures;

import VCS.Exceptions.UnderflowException;

public class LinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int numOfElements = 0;

    public LinkedList() {}

    public LinkedList(T[] elements) {
        addAll(elements, 0, elements.length);
    }

    public void add(T element) {
        if (numOfElements == 0) {
            head = new Node<T>(element);
            tail = head;
        }
        else {
            tail.setNext(new Node<T>(element));
            tail = tail.getNext();
        }
        numOfElements++;
    }

    public void add(T element, int index) {
        if (index > numOfElements) {
            throw new IndexOutOfBoundsException();
        }
        else {
            Node<T> node = new Node<>(element);
            if (index == 0) {
                node.setNext(head);
                head = node;
            } else if (index == numOfElements) {
                tail.setNext(node);
                tail = tail.getNext();
            } else {
                Node<T> current = head;

                for (int i = 0; i < index - 1; i++) {
                    current = current.getNext();
                }

                node.setNext(current.getNext());
                current.setNext(node);
            }
            numOfElements++;
            if (numOfElements == 1) {
                tail = head;
            }
        }
    }

    public void addAll(T[] elements, int start, int end) {
        for (int i = start; i < end; i++) {
            add(elements[i]);
        }
    }

    public void remove(T element) throws UnderflowException {
        if (numOfElements != 0) {
            Node<T> current = head;
            Node<T> predecessor = null;

            while (current != null && !current.getData().equals(element)) {
                predecessor = current;
                current = current.getNext();
            }

            if (current == null) {
                return;
            }
            else {
                if (current == head) {
                    head = head.getNext();
                }
                else {
                    predecessor.setNext(current.getNext());
                }

                if (tail == current) {
                    tail = predecessor;
                }
                numOfElements--;
            }
        }
        else {
            throw new UnderflowException("Cannot remove element from an empty list");
        }
    }

    public void remove(T element, int from) throws UnderflowException {
        if (from > numOfElements) {
            throw new IndexOutOfBoundsException();
        }
        else {
            Node<T> current = head;
            for (int i = 0; i < from; i++) {
                current = current.getNext();
            }
            Node<T> tempHead = head;
            head = current;
            remove(element);
            head = tempHead;
        }
    }

    public int size() {
        return numOfElements;
    }

    public T[] toArray(T[] elements) {
        Node<T> current = head;

        for (int i = 0; i < numOfElements; i++) {
            elements[i] = current.getData();
            current = current.getNext();
        }

        return elements;
    }

    public Iterator iterator() {
        return new Iterator();
    }

    public boolean isEmpty() {
        return numOfElements == 0;
    }

    public class Iterator {
        Node<T> current;

        Iterator() {
            current = head;
        }

        public T next() {
            T data =  current.getData();
            current = current.getNext();
            return data;
        }

        public boolean hasNext() {
            return current != null;
        }
    }
}
