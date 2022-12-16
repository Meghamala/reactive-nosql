# reactive-nosql
NoSQL databases are used for unstructured data. In this project, I have created a mini-in-memory database. 

For building NoSQL DB, I have used HashMap data structure to store key value pairs. Key is a string and value can be any data including Strings, Numbers, “Arrays” and “Objects”.

DB operations added:

> Put , Get, GetInt, GetDouble, GetString, GetArray, GetObject, remove and update for basic manipulation.

Multiple Object oriented Design Patterns have been used for efficient implementation of system.

For supporting Transaction having functionality of rollback via undo > Command pattern
For supporting data availability as NoSQL is not persistent via snapshot creation and recovery mechanism > Memento pattern
For enhancing effective reactivenenss of NoSQL via introduction of cursor feature > Observer pattern




