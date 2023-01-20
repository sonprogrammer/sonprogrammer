package com.study.board.service;

import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    //글 작성 처리
    public void write(Board board, MultipartFile file) throws Exception{

        String projectpath = System.getProperty("user.dir") + "/src/main/webapp/";
        //저장할 경료를 지정해줌

        UUID uuid = UUID.randomUUID();
        //파일이름에 붙일 랜덤 이름을 생성해줌

        String fileName = uuid + "_" + file.getOriginalFilename();
        //랜덤으로 생성된 파일이름을 앞에 붙이고 _ 하고 들어온 원래 파일이름을 생성해줌

        File saveFile = new File(projectpath, fileName);

        file.transferTo(saveFile);

        board.setFilename(fileName);
        board.setFilepath("/webapp/" + fileName);
        boardRepository.save(board);
    }
    //게시글 리스트 처리
    public Page<Board> boardList(Pageable pageable){

        return boardRepository.findAll(pageable);
    }

    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable){

        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    //특정 게시글 불러오기
    public Board boardview(Integer id){
        return boardRepository.findById(id).get();
    }

    //특정 게시글 삭제
    public void boardDelete(Integer id){  //void는 return 타입이 없는걸 의미한다
        boardRepository.deleteById(id);
    }
}
