package kz.itolstov.demo.controller.view;

import kz.itolstov.demo.model.BaseEntity;
import kz.itolstov.demo.model.Item;
import kz.itolstov.demo.service.AuctionBaseService;
import kz.itolstov.demo.service.UserService;
import kz.itolstov.demo.service.UserServiceImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
public class ViewController {

    //todo перевести в .properties
    public final String STORAGE_PATH = System.getProperty("user.dir") + "/storage";
    public final String STORAGE_IMAGE_PATH = STORAGE_PATH + "/images";

    private UserService userService;

    @GetMapping("/new-item")
    public String newItemForm(Model model) {
        model.addAttribute("item", new Item());
        return "newitem";
    }

    @PostMapping("/new-item")
    public ModelAndView postNewItem(
            @RequestParam Long owner_id,
            @ModelAttribute Item item,
            ModelMap model
    ) {
        item.setOwner(userService.findById(owner_id));
        item.setStatus(Item.Status.ACTIVE);
        return new ModelAndView("redirect:/uploadImg", model);
    }

    @GetMapping("/uploadImg")
    public String uploadImageForm(Model model) {
        return "uploadimg";
    }

    @PostMapping("/uploadImg")
    public String uploadImage(
            @RequestParam MultipartFile image,
            Model model
    ) throws IOException {
        StringBuilder filenames = new StringBuilder();
        Path path = Paths.get(STORAGE_IMAGE_PATH, image.getOriginalFilename());
        filenames.append(image.getOriginalFilename());
        Files.write(path, image.getBytes());

        model.addAttribute("msg", "Uploaded images: " + filenames);

        return "uploadimg";
    }


}
