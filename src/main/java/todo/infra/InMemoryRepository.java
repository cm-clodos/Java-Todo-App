package todo.infra;

import todo.model.TodoItem;
import todo.service.TodoRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRepository implements TodoRepository {
    List<TodoItem> todos = new ArrayList<>();

    public InMemoryRepository() {

        todos.add(TodoItem.create(1L, "Kochen"));
        todos.add(TodoItem.create(2L, "Radeln"));
        todos.add(TodoItem.create(3L, "Einkaufen"));
        todos.add(TodoItem.create(4L, "Reise buchen"));
        todos.add(TodoItem.create(5L, "Wäsche waschen"));
    }

    @Override
    public List<TodoItem> getAll() {
        return todos;
    }
}
