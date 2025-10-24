package com.technicaltest.project_be.controller;

import com.technicaltest.project_be.request.ReviewRequest;
import com.technicaltest.project_be.request.ReviewUpdateRequest;
import com.technicaltest.project_be.response.DTO.BookResponse;
import com.technicaltest.project_be.response.internal.EnrichedReviewResponse;
import com.technicaltest.project_be.response.internal.ReviewMessageResponse;
import com.technicaltest.project_be.service.IBookService;
import com.technicaltest.project_be.service.IReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController()
@RequestMapping("")
@Validated
@Tag(name = "Book Controller", description = "API for managing books and reviews")
public class BookController {

  private static final Logger logger = LoggerFactory.getLogger(BookController.class);

  @Autowired
  private IBookService bookService;

  @Autowired
  private IReviewService reviewService;

  @GetMapping("/books")
  @Operation(
      summary = "Search books",
      description = "Returns a list of books based on search parameter using external API",
      parameters = {
          @Parameter(name = "search", description = "Search term to filter books", required = true, example = "Harry Potter")
      }
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Books found successfully",
          content = @Content(schema = @Schema(implementation = BookResponse.class))),
      @ApiResponse(responseCode = "204", description = "No books found for the search term"),
      @ApiResponse(responseCode = "500", description = "Internal server error during search")
  })
  public Mono<ResponseEntity<List<BookResponse>>> getBooks(@RequestParam String search) {
    logger.info("Start getBooks - search: {}", search);
    return bookService.retriveBookByQuery(search)
        .map(books -> switch (books.isEmpty() ? "EMPTY" : "FOUND") {
          case "EMPTY" -> ResponseEntity.noContent().build();
          case "FOUND" -> ResponseEntity.ok(books);
          default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        });
  }

  @PostMapping("/review")
  @Operation(
      summary = "Create a new review",
      description = "Creates and submits a new review for a book. The review is validated and processed asynchronously."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Review created and accepted successfully",
          content = @Content(schema = @Schema(implementation = ReviewMessageResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request, book does not exist or sending error"),
      @ApiResponse(responseCode = "500", description = "Internal server error during processing")
  })
  public ResponseEntity<ReviewMessageResponse> reviewBook(
      @Parameter(description = "Review data to create", required = true)
      @Valid @RequestBody ReviewRequest request) {
    logger.info("Start reviewBook - bookId: {}", request.getId());
    ReviewMessageResponse response = reviewService.checkAndSendReview(request);

    return switch (response.getStatus()) {
      case DELIVERED, QUEUED -> ResponseEntity.status(HttpStatus.OK).body(response);
      case SENT_ERROR, BOOK_NOT_EXIST -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      case PROCESSING_ERROR -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      default -> {
        yield ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }
    };
  }

  @GetMapping("/review/{id}")
  @Operation(
      summary = "Get a review",
      description = "Retrieves details of a specific review by ID. Returns 202 if still processing, 200 if completed."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Review processed and returned with enriched data",
          content = @Content(schema = @Schema(implementation = EnrichedReviewResponse.class))),
      @ApiResponse(responseCode = "202", description = "Review queued for processing"),
      @ApiResponse(responseCode = "404", description = "Review not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<EnrichedReviewResponse> reviewReview(
      @Parameter(description = "Unique review ID", required = true, example = "12345")
      @PathVariable String id) {
    logger.info("Start reviewReview - reviewId: {}", id);
    EnrichedReviewResponse response = this.reviewService.getReviewById(id);
    return switch (response.getStatus()) {
      case QUEUED -> ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
      case PROCESSED -> ResponseEntity.status(HttpStatus.OK).body(response);
      default -> {
        yield ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }
    };
  }

  @PutMapping("/review/{id}")
  @Operation(
      summary = "Update a review",
      description = "Updates the data of an existing review (score and review text)"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Review updated successfully",
          content = @Content(schema = @Schema(implementation = EnrichedReviewResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request data"),
      @ApiResponse(responseCode = "404", description = "Review not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<EnrichedReviewResponse> updateReview(
      @Parameter(description = "Unique review ID", required = true, example = "12345")
      @PathVariable String id,
      @Parameter(description = "Review data to update", required = true)
      @Valid @RequestBody ReviewUpdateRequest request) {
    logger.info("Start updateReview - reviewId: {}", id);
    return ResponseEntity.status(HttpStatus.OK).body(this.reviewService.updateReview(request, id));
  }

  @DeleteMapping("/review/{id}")
  @Operation(
      summary = "Delete a review",
      description = "Deletes a specific review by ID"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Review deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Review not found"),
      @ApiResponse(responseCode = "500", description = "Internal server error during deletion")
  })
  public ResponseEntity<Void> deleteReview(
      @Parameter(description = "Unique review ID", required = true, example = "12345")
      @PathVariable String id) {
    logger.info("Start deleteReview - reviewId: {}", id);
    this.reviewService.deleteReviewById(id);
    return ResponseEntity.status(HttpStatus.OK).body(null);
  }
}