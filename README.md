# Quotes-Rest API Documentation

This API provides access to a collection of inspirational quotes. Below you'll find the API reference and details about the API's authors.

## API Reference

### Get Random Quotes

Retrieve random quotes. Optionally specify the number of quotes to return using the `count` parameter.

```http
GET /random?count={number_of_quotes}
```

Parameters:
- `number_of_quotes` (integer): The number of random quotes to retrieve. If omitted, defaults to 1.

Examples:
- To retrieve a single random quote: `/random`
- To retrieve five random quotes: `/random?count=5`

## Authors

- [tilalx](https://www.github.com/tilalx)