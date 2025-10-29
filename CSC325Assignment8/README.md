# Refactor summary 

**Goals of the refactor:**
Refactor Using Streams and Lambdas
- Replace for/while loops with .stream() methods
- Use chaining methods such as filter(), map(), sorted(), collect()
- Avoid mutation or imperative list modifications
- Maintain clear, readable formatting
- Apply at least two of the following advanced features:
        - Optional<>
        - Comparator.comparing()
        - reduce(), flatMap(), or distinct()
- Add a commented section explaining each feature’s purpose

**What was changed:**
- Methods rewritten using streams: getHonorRoll, getEmailsByMajor, getTopNByGpa, getAverageGpa, findById, getDistinctCourseTitles.
- Sorting handled through Comparator.comparing().thenComparing()
- null checks moved to stream filters
- flatMap() used for nested collections
- distinct() used to eliminate duplicates

**Benefits:**
- Clarity was improved as each method is shorter and straightforward.
- Code is easier to expand on and change in the future because of the modular nature and readability of streams.
- Reduced potential for bugs by minimizing changes that can be made to the existing data




