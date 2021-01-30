package cn.smallyoung.websiteadmin.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.entity.Note;
import cn.smallyoung.websiteadmin.entity.NoteMenus;
import cn.smallyoung.websiteadmin.interfaces.ResponseResultBody;
import cn.smallyoung.websiteadmin.service.NoteMenusService;
import cn.smallyoung.websiteadmin.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
@PreAuthorize("hasAnyRole('ROLE_NOTE')")
public class NoteController {

    @Resource
    private NoteService noteService;
    @Resource
    private NoteMenusService noteMenusService;

    @GetMapping
    public Dict index(HttpServletResponse response){
        getUserId(response);
        List<NoteMenus> noteMenusList = noteMenusService.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
        String content = "";
        if(CollUtil.isNotEmpty(noteMenusList)){
            Optional<Note> note = noteService.findById(noteMenusList.get(0).getId());
            if(note.isPresent()){
                content = note.get().getContent();
            }
        }
        return Dict.create().set("menus", noteMenusList)
                .set("content", StrUtil.isNotBlank(content) ? EscapeUtil.escape(content) : "");
    }

    /**
     * 更改Markdown内容
     *
     * @param id          笔记ID
     * @param mdContent   Markdown内容
     */
    @PostMapping("updateContent")
    public Note updateContent(String id, String mdContent, HttpServletResponse response) {
        getNoteMenus(id, response);
        Note note = new Note();
        Optional<Note> optional = noteService.findById(id);
        if(optional.isPresent()){
            note = optional.get();
        }
        note.setContent(mdContent);
        note.setUpdateTime(LocalDateTime.now());
        return noteService.save(note);
    }

    @DeleteMapping("delMenus")
    public void delMenus(String id, HttpServletResponse response){
        NoteMenus menus = getNoteMenus(id, response);
        menus.setIsDelete("Y");
        menus.setUpdateTime(LocalDateTime.now());
        noteMenusService.save(menus);
    }

    @PostMapping("addMenus")
    public NoteMenus addMenus(String name, HttpServletResponse response){
        if(StrUtil.hasBlank(name)){
            log.error("参数错误");
            throw new RuntimeException("参数错误");
        }
        NoteMenus menus = new NoteMenus();
        menus.setUserId(getUserId(response));
        menus.setName(name);
        menus.setIsDelete("Y");
        menus.setCreateTime(LocalDateTime.now());
        menus.setUpdateTime(LocalDateTime.now());
        return noteMenusService.save(menus);
    }

    @PostMapping("updateMenus")
    public NoteMenus updateMenus(String id, String name, HttpServletResponse response){
        if(StrUtil.hasBlank(id, name)){
            log.error("参数错误");
            throw new RuntimeException("参数错误");
        }
        NoteMenus menus = getNoteMenus(id, response);
        menus.setName(name);
        menus.setUpdateTime(LocalDateTime.now());
        return noteMenusService.save(menus);
    }

    private NoteMenus getNoteMenus(String id, HttpServletResponse response){
        if(StrUtil.isBlank(id)){
            log.error("参数错误");
            throw new RuntimeException("参数错误");
        }
        String userId = getUserId(response);
        NoteMenus noteMenus = noteMenusService.findByIdAndUserId(id, userId);
        if(noteMenus == null){
            log.error("未获取笔记菜单");
            throw new RuntimeException("未获取笔记菜单");
        }
        return noteMenus;
    }

    private String getUserId(HttpServletResponse response){
        String userId = response.getHeader("userId");
        if(StrUtil.isBlank(userId)){
            log.error("未获取到用户信息");
            throw new RuntimeException("未获取到用户信息");
        }
        return userId;
    }
}
