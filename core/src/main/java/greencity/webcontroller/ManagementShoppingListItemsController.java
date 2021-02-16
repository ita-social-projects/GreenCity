package greencity.webcontroller;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.shoppinglistitem.*;
import greencity.service.ShoppingListItemService;
import greencity.service.LanguageService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("/management/shopping-list-items")
public class ManagementShoppingListItemsController {
    private final ShoppingListItemService shoppingListItemService;
    private final LanguageService languageService;

    /**
     * Method that returns management page with all {@link ShoppingListItemVO}.
     *
     * @param query    Query for searching related data
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     */
    @GetMapping
    public String getAllShoppingListItems(@RequestParam(required = false, name = "query") String query,
        Pageable pageable,
        Model model) {
        Pageable paging = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").ascending());
        PageableAdvancedDto<ShoppingListItemManagementDto> pageableDto = query == null || query.isEmpty()
            ? shoppingListItemService.findShoppingListItemsForManagementByPage(paging)
            : shoppingListItemService.searchBy(paging, query);
        model.addAttribute("shoppingListItems", pageableDto);
        model.addAttribute("languages", languageService.getAllLanguages());
        return "core/management_shopping_list_items";
    }

    /**
     * The method which save Shopping list item {@link ShoppingListItemVO}.
     *
     * @param shoppingListItemPostDto {@link ShoppingListItemPostDto}
     * @return {@link ResponseEntity}
     * @author Dmytro Khonko
     */
    @PostMapping
    @ResponseBody
    public GenericResponseDto save(@Valid @RequestBody ShoppingListItemPostDto shoppingListItemPostDto,
        BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            shoppingListItemService.saveShoppingListItem(shoppingListItemPostDto);
        }
        return GenericResponseDto.buildGenericResponseDto(bindingResult);
    }

    /**
     * The method which update {@link ShoppingListItemTranslationVO}.
     *
     * @param shoppingListItemPostDto {@link ShoppingListItemPostDto}
     * @return {@link ResponseEntity}
     * @author Dmytro Khonko
     */
    @PutMapping("/{id}")
    @ResponseBody
    public GenericResponseDto update(
        @Valid @RequestBody ShoppingListItemPostDto shoppingListItemPostDto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            shoppingListItemService.update(shoppingListItemPostDto);
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method to find shopping list item by id.
     *
     * @return {@link ShoppingListItemVO} instance.
     * @author Dmytro Khonko
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShoppingListItemResponseDto> getShoppingListItemById(
        @PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(shoppingListItemService.findShoppingListItemById(id));
    }

    /**
     * The method which delete {@link ShoppingListItemVO}.
     *
     * @param id of {@link ShoppingListItemVO}
     * @return {@link ResponseEntity}
     * @author Dmytro Khonko
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(shoppingListItemService.delete(id));
    }

    /**
     * Method which deletes {@link ShoppingListItemVO} and
     * {@link ShoppingListItemTranslationVO} by given id.
     *
     * @param listId list of IDs
     * @return {@link ResponseEntity}
     * @author Dmytro Khonko
     */
    @DeleteMapping("/deleteAll")
    @ResponseBody
    public ResponseEntity<List<Long>> deleteAll(@RequestBody List<Long> listId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(shoppingListItemService.deleteAllShoppingListItemsByListOfId(listId));
    }

    /**
     * Method that returns management page with filtered {@link ShoppingListItemVO}.
     *
     * @param model    Model that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     */
    @PostMapping(value = "/filter")
    public String filterData(Model model, @PageableDefault(value = 20) @ApiIgnore Pageable pageable,
        ShoppingListItemViewDto goal) {
        PageableAdvancedDto<ShoppingListItemManagementDto> pageableDto =
            shoppingListItemService.getFilteredDataForManagementByPage(
                pageable,
                goal);
        model.addAttribute("shoppingListItems", pageableDto);
        model.addAttribute("languages", languageService.getAllLanguages());
        model.addAttribute("fields", goal);
        return "core/management_shopping_list_items";
    }
}
