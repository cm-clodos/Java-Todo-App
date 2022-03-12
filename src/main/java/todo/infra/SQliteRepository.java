package todo.infra;

import todo.model.TodoItem;
import todo.service.TodoRepository;

import java.util.ArrayList;
import java.util.List;

public class SQliteRepository implements TodoRepository {

    @Override
    public List<TodoItem> getAll() {
        return new ArrayList<>();
    }
}
