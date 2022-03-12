package todo.service;

import todo.model.TodoItem;

import java.util.List;

public interface TodoRepository {
    List<TodoItem> getAll();
}
