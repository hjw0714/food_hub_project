package com.application.foodhub.banner;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BannerDAO {
	
	public List<BannerDTO> selectAllBanners();

}
