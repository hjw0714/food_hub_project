package com.application.foodhub.banner;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BannerServiceImpl implements BannerService {
	
	@Autowired
	private BannerDAO bannerDAO;

	@Override
	public List<BannerDTO> selectAllBanners() {
		return bannerDAO.selectAllBanners();
	}

}
