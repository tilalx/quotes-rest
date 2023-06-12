// Powered by Quotable
// https://github.com/lukePeavey/quotable

document.addEventListener("DOMContentLoaded", () => {
  // DOM elements
  const button = document.querySelector("button");
  const quote = document.querySelector("blockquote p");
  const cite = document.querySelector("blockquote cite");

  async function updateQuote() {
    try {
      // Fetch a random quote from the `/random` endpoint
      const response = await fetch("/random");
      const data = await response.json();

      // Check if the response is successful and there is at least one quote
      if (response.ok && data.length > 0) {
        // Extract the first quote object from the response
        const firstQuote = data[0];

        // Extract the desired fields from the first quote object
        const { content, author } = firstQuote;

        // Update DOM elements
        quote.textContent = content;
        cite.textContent = author;
      } else {
        quote.textContent = "An error occurred";
        console.log(data);
      }
    } catch (error) {
      quote.textContent = "An error occurred";
      console.error(error);
    }
  }

  // Attach an event listener to the `button`
  button.addEventListener("click", updateQuote);

  // Call updateQuote once when the page loads
  updateQuote();
});
