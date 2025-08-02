# Profile Markdown File

This file contains examples of how to include JSON code blocks in Markdown.

## Simple JSON Example

```json
{
  "name": "John Doe",
  "age": 30,
  "email": "john.doe@example.com",
  "skills": [
    "Java",
    "Spring Boot",
    "React"
  ]
}
```

## Complex JSON Example

```json
{
  "users": [
    {
      "id": 1,
      "name": "Alice",
      "profile": {
        "position": "Software Engineer",
        "experience": 5,
        "skills": ["Java", "Python", "SQL"]
      }
    },
    {
      "id": 2,
      "name": "Bob",
      "profile": {
        "position": "Frontend Developer",
        "experience": 3,
        "skills": ["JavaScript", "React", "CSS"]
      }
    }
  ],
  "metadata": {
    "total": 2,
    "page": 1
  }
}
```

## Inline JSON

You can also include inline JSON like this: `{"key": "value", "count": 10}` in the middle of a sentence.