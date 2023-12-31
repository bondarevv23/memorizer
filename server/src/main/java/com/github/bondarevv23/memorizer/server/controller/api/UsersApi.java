/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.github.bondarevv23.memorizer.server.controller.api;

import com.github.bondarevv23.memorizer.server.model.generated.DeckDTO;
import com.github.bondarevv23.memorizer.server.model.generated.UserDTO;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Controller
@Tag(name = "users", description = "the users API")
public interface UsersApi {

    /**
     * POST /users/{id}
     * create new user
     *
     * @param id ID of user to fetch (required)
     * @return user created (status code 200)
     *         or user with this is already exists (status code 409)
     */
    @Operation(
        operationId = "addNewUser",
        description = "create new user",
        tags = { "users" },
        responses = {
            @ApiResponse(responseCode = "200", description = "user created", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
            }),
            @ApiResponse(responseCode = "409", description = "user with this is already exists")
        }
    )
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/users/{id}",
        produces = { "application/json" }
    )
    ResponseEntity<UserDTO> addNewUser(
        @Parameter(name = "id", description = "ID of user to fetch", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id
    );


    /**
     * DELETE /users/{id}
     * delete user by id
     *
     * @param id ID of user to fetch (required)
     * @return user successfully deleted (status code 200)
     *         or user with this doesn&#39;t exist (status code 404)
     */
    @Operation(
        operationId = "deleteUserById",
        description = "delete user by id",
        tags = { "users" },
        responses = {
            @ApiResponse(responseCode = "200", description = "user successfully deleted"),
            @ApiResponse(responseCode = "404", description = "user with this doesn't exist")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/users/{id}"
    )
    ResponseEntity<Void> deleteUserById(
        @Parameter(name = "id", description = "ID of user to fetch", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id
    );


    /**
     * GET /users/{id}
     * returns all decks owned by user
     *
     * @param id ID of user to fetch (required)
     * @return a list of decks (status code 200)
     *         or user with this id does not exist (status code 404)
     */
    @Operation(
        operationId = "getDecksByUserId",
        description = "returns all decks owned by user",
        tags = { "users" },
        responses = {
            @ApiResponse(responseCode = "200", description = "a list of decks", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DeckDTO.class)))
            }),
            @ApiResponse(responseCode = "404", description = "user with this id does not exist")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/users/{id}",
        produces = { "application/json" }
    )
    ResponseEntity<List<DeckDTO>> getDecksByUserId(
        @Parameter(name = "id", description = "ID of user to fetch", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id
    );


    /**
     * DELETE /users/{id}/remove/{deckId}
     * remove deck from user
     *
     * @param id ID of user to fetch (required)
     * @param deckId ID of deck to fetch (required)
     * @return the deck was successfully removed from the user (status code 200)
     *         or wrong user ID or deck ID (status code 404)
     *         or the user is the owner of the deck (status code 409)
     */
    @Operation(
        operationId = "removeDeckFromUser",
        description = "remove deck from user",
        tags = { "users" },
        responses = {
            @ApiResponse(responseCode = "200", description = "the deck was successfully removed from the user"),
            @ApiResponse(responseCode = "404", description = "wrong user ID or deck ID"),
            @ApiResponse(responseCode = "409", description = "the user is the owner of the deck")
        }
    )
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/users/{id}/remove/{deckId}"
    )
    ResponseEntity<Void> removeDeckFromUser(
        @Parameter(name = "id", description = "ID of user to fetch", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id,
        @Parameter(name = "deckId", description = "ID of deck to fetch", required = true, in = ParameterIn.PATH) @PathVariable("deckId") Integer deckId
    );


    /**
     * PUT /users/{id}/share/{deckId}
     * share deck with user
     *
     * @param id ID of user to fetch (required)
     * @param deckId ID of deck to fetch (required)
     * @return the deck was successfully added to the user (status code 200)
     *         or wrong user ID or deck ID (status code 404)
     *         or this user already har this deck (status code 409)
     */
    @Operation(
        operationId = "shareDeckWithUser",
        description = "share deck with user",
        tags = { "users" },
        responses = {
            @ApiResponse(responseCode = "200", description = "the deck was successfully added to the user"),
            @ApiResponse(responseCode = "404", description = "wrong user ID or deck ID"),
            @ApiResponse(responseCode = "409", description = "this user already har this deck")
        }
    )
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/users/{id}/share/{deckId}"
    )
    ResponseEntity<Void> shareDeckWithUser(
        @Parameter(name = "id", description = "ID of user to fetch", required = true, in = ParameterIn.PATH) @PathVariable("id") Long id,
        @Parameter(name = "deckId", description = "ID of deck to fetch", required = true, in = ParameterIn.PATH) @PathVariable("deckId") Integer deckId
    );

}
