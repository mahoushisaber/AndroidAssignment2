package ca.bcit.ass2.lee_dong;

import java.util.Date;

public class ToDo {
    String taskID;
    String task;
    String who;
    Date dueDate;

    public ToDo() {
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public ToDo(String ID, String task, String who, Date dueDate ) {
        this.taskID = ID;
        this.task = task;
        this.who = who;
        this.dueDate = dueDate;


    }
}
