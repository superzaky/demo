package com.example.demo.controller;

import com.example.demo.model.Todo;
import com.example.demo.model.User;
import com.example.demo.repository.TodoRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all to-dos for the authenticated user
    @GetMapping
    public List<Todo> getTodos(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return todoRepository.findByUser(user);
    }

    // Create a new to-do for the authenticated user
    @PostMapping
    public Todo createTodo(Authentication authentication, @RequestBody Todo todo) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    // Update an existing to-do for the authenticated user
    @PutMapping("/{id}")
    public Todo updateTodo(Authentication authentication, @PathVariable Long id, @RequestBody Todo todoRequest) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        if (!todo.getUser().getUsername().equals(authentication.getName())) {
            throw new RuntimeException("You cannot update this todo");
        }
        todo.setTitle(todoRequest.getTitle());
        todo.setDescription(todoRequest.getDescription());
        return todoRepository.save(todo);
    }

    // Delete a to-do for the authenticated user
    @DeleteMapping("/{id}")
    public void deleteTodo(Authentication authentication, @PathVariable Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        if (!todo.getUser().getUsername().equals(authentication.getName())) {
            throw new RuntimeException("You cannot delete this todo");
        }
        todoRepository.delete(todo);
    }
}

