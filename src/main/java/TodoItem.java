import java.util.ArrayList;
import java.util.List;

public class TodoItem {
    public String description;
    public Long id;
    public static TodoItem create(String description, Long id) {
        TodoItem todoItem = new TodoItem();
        todoItem.description = description;
        todoItem.id = id;

        return todoItem;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "description='" + description + '\'' +
                ", id=" + id +
                '}';
    }
}
