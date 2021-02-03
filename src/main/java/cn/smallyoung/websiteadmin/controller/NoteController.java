package cn.smallyoung.websiteadmin.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.Note;
import cn.smallyoung.websiteadmin.entity.NoteMenus;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.ArticleService;
import cn.smallyoung.websiteadmin.service.NoteMenusService;
import cn.smallyoung.websiteadmin.service.NoteService;
import cn.smallyoung.websiteadmin.service.SysUserService;
import com.upyun.UpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author smallyoung
 * @data 2021/1/27
 */
@Slf4j
@RestController
@ResponseResultBody
@RequestMapping("note")
@PreAuthorize("hasRole('ROLE_NOTE')")
public class NoteController {

    @Resource
    private NoteService noteService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ArticleService articleService;
    @Resource
    private NoteMenusService noteMenusService;

    @GetMapping
    public Dict index() {
        List<NoteMenus> noteMenusList = noteMenusService.findByUserId(sysUserService.currentlyLoggedInUser());
        String content = "";
        if (CollUtil.isNotEmpty(noteMenusList)) {
            Optional<Note> note = noteService.findById(noteMenusList.get(0).getId());
            if (note.isPresent()) {
                content = note.get().getContent();
            }
        }
        return Dict.create().set("menus", noteMenusList)
                .set("content", StrUtil.isNotBlank(content) ? EscapeUtil.escape(content) : "");
    }

    /**
     * 获取笔记内容
     *
     * @param id 文章ID
     */
    @GetMapping("getContentById")
    public String getContentById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new NullPointerException("参数错误");
        }
        Optional<Note> optional = noteService.findById(id);
        return optional.map(note -> EscapeUtil.escape(note.getContent())).orElse(null);
    }

    /**
     * 更改Markdown内容
     *
     * @param id      笔记ID
     * @param content Markdown内容
     */
    @PostMapping("updateContent")
    public Note updateContent(String id, String content) {
        getNoteMenus(id);
        Note note = new Note();
        Optional<Note> optional = noteService.findById(id);
        if (optional.isPresent()) {
            note = optional.get();
        }
        note.setId(id);
        note.setContent(content);
        note.setUpdateTime(LocalDateTime.now());
        return noteService.save(note);
    }

    @DeleteMapping("delMenus")
    public void delMenus(String id) {
        NoteMenus menus = getNoteMenus(id);
        menus.setIsDelete("Y");
        menus.setUpdateTime(LocalDateTime.now());
        noteMenusService.save(menus);
    }

    @PostMapping("addMenus")
    public NoteMenus addMenus(String name) {
        if (StrUtil.hasBlank(name)) {
            log.error("参数错误");
            throw new RuntimeException("参数错误");
        }
        NoteMenus menus = new NoteMenus();
        menus.setUserId(sysUserService.currentlyLoggedInUser());
        menus.setName(name);
        menus.setIsDelete("N");
        menus.setCreateTime(LocalDateTime.now());
        menus.setUpdateTime(LocalDateTime.now());
        return noteMenusService.save(menus);
    }

    @PostMapping("updateMenus")
    public NoteMenus updateMenus(String id, String name) {
        if (StrUtil.hasBlank(id, name)) {
            log.error("参数错误");
            throw new RuntimeException("参数错误");
        }
        NoteMenus menus = getNoteMenus(id);
        menus.setName(name);
        menus.setUpdateTime(LocalDateTime.now());
        return noteMenusService.save(menus);
    }

    @PostMapping("uploadImg/{path}")
    public String uploadImg(MultipartFile file, @PathVariable String path) throws IOException, UpException {
        return articleService.uploadImg(file, "/note/" + sysUserService.currentlyLoggedInUser() + "/" + path + "/");
    }

    private NoteMenus getNoteMenus(String id) {
        if (StrUtil.isBlank(id)) {
            log.error("参数错误");
            throw new RuntimeException("参数错误");
        }
        String userId = sysUserService.currentlyLoggedInUser();
        NoteMenus noteMenus = noteMenusService.findByIdAndUserId(id, userId);
        if (noteMenus == null) {
            log.error("未获取笔记菜单");
            throw new RuntimeException("未获取笔记菜单");
        }
        return noteMenus;
    }
}
