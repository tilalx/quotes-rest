IF OBJECT_ID(N'dbo.quotes', N'U') IS NULL
	Create table dbo.quotes (
    id nchar (30) UNIQUE NOT NULL,
    content nchar (500) NOT NULL,
    author nchar (50) NOT NULL,
	tags nchar (100) NULL,
    authorSlug nchar (50) NOT NULL,
    length int NOT NULL,
    dateadded DATETIME NOT NULL,
    datemodified DATETIME NOT NULL,
)
