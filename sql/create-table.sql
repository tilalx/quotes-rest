Create table quotes (
    id nchar (30) UNIQUE NOT NULL,
    content nchar (500) NOT NULL,
    author nchar (50) NOT NULL,
    authorSlug nchar (50) NULL,
    length int NOT NULL,
    dateadded DATETIME NOT NULL,
)