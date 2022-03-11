package todo.model;

public class TodoItem {
    public static int  counter;
    public String description;
    public Long id;



    public TodoItem(){
        this.id =(long) (counter++ + 1);
    }
    public TodoItem(String description){
        this.id =(long) (counter++ + 1);
        this.description = description;
    }


    public static TodoItem create(Long id, String description) {
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
        return "todo.model.TodoItem{" +
                "description='" + description + '\'' +
                ", id=" + id +
                '}';
    }
}
