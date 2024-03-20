package edu.iu.c322.midterm.controllers;

import edu.iu.c322.midterm.model.Quiz;
import edu.iu.c322.midterm.repository.FileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/quizzes")
public class QuizController {
    private FileRepository fileRepository;

    public QuizController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostMapping
    public int addQuiz(@RequestBody Quiz quiz) throws IOException {
        try {
            return fileRepository.add(quiz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public List<Quiz> findAllQuizzes() {
        try {
            return fileRepository.findAllQuizzes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}")
    public Quiz getQuiz(@PathVariable int id) {
        try {
            return fileRepository.getQuiz(id);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "404: Quiz not found", e);
        }
    }

    @PutMapping("/{id}")
    public void updateQuiz(@PathVariable int id, @RequestBody Quiz newQuiz) {
        try {
            fileRepository.updateQuiz(id, newQuiz);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "404: Quiz not found", e);
        }
    }
}
