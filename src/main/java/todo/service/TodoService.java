package todo.service;

import todo.model.TodoItem;

import java.util.List;

public class TodoService {
    private final TodoRepository repo;



    public TodoService(TodoRepository repo) {
        this.repo = repo;
    }

    public TodoItem getById(Long id) {
        for (var item : repo.getAll()) {
            if (item.id.equals(id)) {
                return item;
            }
        }
        return null;
    }


    public List<TodoItem> getAllTodos() {
        return repo.getAll();
    }
    public Boolean removeItemById(Long id){
        boolean removed = repo.getAll().removeIf(todoItem -> todoItem.id.equals(id));
        return removed;

    }
}



