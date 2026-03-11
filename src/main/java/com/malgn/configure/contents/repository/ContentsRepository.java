package com.malgn.configure.contents.repository;

import com.malgn.configure.contents.entity.Contents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentsRepository extends JpaRepository<Contents,Long>, ContentsRepositoryCustom {
}
