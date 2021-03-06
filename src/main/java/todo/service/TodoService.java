package todo.service;

import todo.model.TodoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public List<TodoItem> getFilterTodo(String query) {
        ArrayList<TodoItem> foundTodos = new ArrayList<>();
        if (!query.trim().isEmpty()) {
            for (TodoItem todoItem : getAllTodos()) {
                if (todoItem.description.toLowerCase(Locale.ROOT).contains(query)) {
                    foundTodos.add(todoItem);

                }
            }
        }
        return foundTodos;
        }




    public Boolean createItem(TodoItem item) {

        if(item.description.trim().isEmpty()){
            return false;
        }else {
            int sizeBefore = this.getAllTodos().size();
            this.getAllTodos().add(item);
            int sizeAfter = this.getAllTodos().size();

            if (sizeAfter > sizeBefore){

                return true;
            }
        }

        return false;


    }
}



