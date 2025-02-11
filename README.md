# Toy Git

This is a simplified version of Git implemented in Java, providing basic functionality such as initializing a repository, reading and writing objects, and managing commits. The project is designed to help explore and understand the low-level mechanisms of Git, including the structure of Git objects, how data is stored, and how basic Git operations work.

## Features

- **`init`**: Initializes a new Git repository with the necessary directory structure and files.
- **`cat-file`**: Displays the content of Git objects (blobs, trees, commits) based on their hash.
- **`hash-object`**: Computes the SHA-1 hash of a file and stores it as a blob object.
- **`write-tree`**: Creates a tree object representing the current state of the repository's files.
- **`ls-tree`**: Lists the contents of a tree object, showing the files and directories in a human-readable format.
- **`commit`**: Creates a commit object, linking a tree object and a parent commit, storing the commit message.

## Insights

While implementing this toy version of Git, I gained a deeper understanding of how version control systems, especially Git, operate at a low level. Specifically, I learned:

- **Git Objects** : How Git uses different types of objects: blobs, trees, and commits
- **SHA-1 Hashing**: The significance of hashing in Git. Each object’s content is hashed, and that hash is used as a unique identifier
- **Low-Level Operations**: The implementations of these commands are not identical to official Git but follow the same principles

## Limitations

- This toy Git project is not as fully featured or optimized as the official Git
- The focus is on learning and exploring Git’s low-level mechanics rather than mimicking the full functionality of the official tool