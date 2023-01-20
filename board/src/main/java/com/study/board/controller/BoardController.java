package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;

import java.io.IOException;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;
    @GetMapping("/board/write") //localhost:8080/board/write 주소로 가면 boardwrite.html이 뜬다
    public String boardwriteForm(){

        return "boardwrite";
    }
    @PostMapping("/board/writePro")
    public String boardwritePro(Board board, Model model, MultipartFile file) throws Exception{
        boardService. write(board, file);
        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }
    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size =5, sort = "id",
                                    direction = Sort.Direction.DESC) Pageable pageable
                            , String searchKeyword){ //데이터를 담아서 우리가 보내는 페이지로 보내야하는데 그 때 쓰는게 model이다

        Page<Board> list = null;

        if(searchKeyword == null){
            list = boardService.boardList(pageable);
        }else{
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        //Page<Board> list = boardService.boardList(pageable);

        int nowPage = list.getPageable().getPageNumber() + 1; //페이지가 0부터 시작하기 때문에 1을 더해서 1페이지부터 시작하게 만들어야함
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());
        //페이지 블록을 처리해주는것

        model.addAttribute("lsit", list);
        model.addAttribute("nowpage", nowPage);
        model.addAttribute("startpage", startPage);
        model.addAttribute("endpage", endPage);
        model.addAttribute("list", boardService.boardList(pageable));

        return "boardList";
    }
    @GetMapping("/board/view") //localhost:8080/board/view?id=1(get방식임)
    //뒤에 있는 1이 Integer id의 id로 들어가고 id에 들어간 1이 boardService.boardview(id) 로 들어가서 게시글을 불러온다(id 값이 1인 게시글을 불러온다)
    public String boardview(Model model, Integer id){
        model.addAttribute( "board", boardService.boardview(id));
        //board라는 이름을 넘겨줄건데 어떤걸 넘겨줄꺼냐 하면 boardService에 boardview 라는걸
        //넘겨줌
        return "boardview";
    }
    @GetMapping("/board/delete")
    public String boardDelete(Integer id, Model model){
        boardService.boardDelete(id);

        model.addAttribute("message", "글이 삭제되었습니다");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model){
        //@PathVariable이란것은 url이 들어왔을 때 '/'뒤에 id부분이 @PathVariable에 인식이 되어서
        //Integer id로 들어온다는 것이다
        model.addAttribute("board", boardService.boardview(id));
        return "boardmodify";

    }
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception {

        Board boardTemp = boardService.boardview(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        boardService.write(boardTemp, file);


       model.addAttribute("message","글 수정이 완료되었습니다!");
       model.addAttribute("searchUrl","/board/list");
        return "message";
    }

}
