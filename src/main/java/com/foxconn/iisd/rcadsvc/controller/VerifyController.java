package com.foxconn.iisd.rcadsvc.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import com.foxconn.iisd.rcadsvc.domain.CodeTable;
import com.foxconn.iisd.rcadsvc.domain.ProductInteractiveSN;
import com.foxconn.iisd.rcadsvc.domain.auth.User;
import com.foxconn.iisd.rcadsvc.msg.RestReturnMsg;
import com.foxconn.iisd.rcadsvc.msg.VerifyMsg;
import com.foxconn.iisd.rcadsvc.repo.CodeTableRepository;
import com.foxconn.iisd.rcadsvc.repo.ProductInteractiveSNRepository;
import com.foxconn.iisd.rcadsvc.repo.UserRepository;
import com.foxconn.iisd.rcadsvc.service.ProductInteractiveSNService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Configuration
@Api(description = "互動式SN驗證")
@RestController
@RequestMapping("/validation")
public class VerifyController {

	private static final Logger logger = LoggerFactory.getLogger(VerifyController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductInteractiveSNService productInteractiveSNService;

	@Autowired
	private ProductInteractiveSNRepository productInteractiveSNRepository;

	@Autowired
	@Qualifier("mysqlJtl")
	private JdbcTemplate mysqlJtl;

	@Autowired
	@Qualifier("cockroachJtl")
	private JdbcTemplate cockroachJtl;

	/**
	 * @param code
	 * @return com.foxconn.iisd.rcadsvc.msg.RestReturnMsg
	 * @date 2019/9/26  09:24
	 * @description 新增互動式SN驗證
	 */
	@ApiOperation("新增互動式SN驗證")
	@RequiresAuthentication
	@PostMapping("")
	public @ResponseBody
	RestReturnMsg create(@RequestBody VerifyMsg verifyMsg) {
		logger.info("===> Create method");
		Integer code = null;
		String msg = null;

		Subject subject = SecurityUtils.getSubject();
		// 檢查用戶是否已驗證 , 是將回傳true
		logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
		HashMap dataMap = new HashMap<String, Object>();

		code = 200;
		if (subject.isAuthenticated()) {
			User currentUser = userRepository
					.findByUsername((String) SecurityUtils.getSubject().getSession()
							.getAttribute(UserController.USERNAME_SESSION_KEY));
			msg = "Create successfully!!";
			List<ProductInteractiveSN> dbcode = productInteractiveSNService.createProductInteractiveSN(currentUser, verifyMsg.toNewDDS());
//            List<VerifyMsg> dataList = new ArrayList<>();
//            for(ProductInteractiveSN item : dbcode){
//            	dataList.add(item.toVerifyMsg(true));
//            }
			dataMap.put("dataCount", dbcode.size());

		} else {
			logger.info("==> This user has not logged in");
			msg = "This user has not logged in";
			dataMap.put("error", "error");
			code = 401;
		}
//        dataMap.put("code", code);
		return new RestReturnMsg(code, msg, dataMap);
	}

	/**
	 * @param id
	 * @return
	 * @date 2019/5/26 下午14:12
	 * @description 用ID 刪除 ProductInteractiveSN
	 */
	@ApiOperation("用ID 刪除 ProductInteractiveSN")
	@RequiresAuthentication
	@DeleteMapping("/sn/{id}/delete")
	public @ResponseBody
	RestReturnMsg deleteById(@PathVariable Long id) {
		Integer code = null;
		String msg = null;

		User currentUser = userRepository
				.findByUsername((String) SecurityUtils.getSubject().getSession()
						.getAttribute(UserController.USERNAME_SESSION_KEY));
		ProductInteractiveSN findOne = productInteractiveSNRepository.findById(id).isPresent() ? productInteractiveSNRepository.findById(id).get() : null;
		if (findOne == null) {
			code = 404;
			msg = "The SN not exists!!";
		} else {
			code = 200;
			msg = "delete SN by id successfully";
			productInteractiveSNService.deleteProductInteractiveSN(currentUser, findOne);
		}
		return new RestReturnMsg(code, msg, id);
	}

	/**
	 * @param id
	 * @return
	 * @date 2019/5/26 下午14:12
	 * @description 條列 byProduct & SN
	 */
	@ApiOperation("條列 byProduct & SN")
	@RequiresAuthentication
	@GetMapping("")
	@RequiresPermissions(value={"fdj_validation:Create","fdj_validation:Update","fdj_validation:Delete","fdj_validation:View",
			"fdj:Create","fdj:Update","fdj:Delete","fdj:View"},logical= Logical.OR)
	public @ResponseBody
	RestReturnMsg list(String product, String sn) {
		List<ProductInteractiveSN> productInteractiveSNList;
		List<VerifyMsg> verifyMsgList = new ArrayList<VerifyMsg>();
		if (product == null) {
			productInteractiveSNList = productInteractiveSNService.findAll();
		} else if (sn != null) {
			productInteractiveSNList = productInteractiveSNService.findByProductAndSn(product, sn);
		} else {
			productInteractiveSNList = productInteractiveSNService.findByProduct(product);
		}
		for (ProductInteractiveSN item : productInteractiveSNList) {
			verifyMsgList.add(item.toVerifyMsg(false));
		}
		return new RestReturnMsg(200, "list SN successfully", verifyMsgList);
	}

	/**
	 * @param id
	 * @return
	 * @date 2019/5/26 下午14:12
	 * @description 取得 cockroach data
	 */
	@ApiOperation("取得 cockroach data master")
	@RequiresAuthentication
	@GetMapping("/sn/{sn}/master")
	public @ResponseBody
	RestReturnMsg getRawData(@PathVariable String sn) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		ZonedDateTime zdt;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		String sql = "select "
				+ "scantime as assemblyTime, "
				+ "id, "
				+ "assembline as testStation, "
				+ "wo, "
				+ "floor "
				+ "from part_master "
				+ "where sn='" + sn + "' "
				+ "order by scantime";
		resultList = cockroachJtl.queryForList(sql);
		for (Map<String, Object> result : resultList) {
			try {
				if (result.get("assemblyTime") != null) {
					try {
						result.replace("assemblyTime", dateFormat.format(result.get("assemblyTime")));
					} catch (Exception e) {
						result.replace("assemblyTime", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", new Locale("Asia/Taipei")).format(ZonedDateTime.parse(result.get("assemblyTime").toString().replace(" ", "T").replace(".0", "+00:00")).toInstant().atZone(ZoneId.systemDefault())));
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		HashMap dataMap = new HashMap<String, Object>();
		dataMap.put("sn", sn);
		dataMap.put("type", "master");
		dataMap.put("result", resultList);
		return new RestReturnMsg(200, "getRawData successfully", dataMap);
	}

	/**
	 * @param id
	 * @return
	 * @date 2019/5/26 下午14:12
	 * @description 取得 cockroach data
	 */
	@ApiOperation("取得 cockroach data test")
	@RequiresAuthentication
	@GetMapping("/sn/{sn}/test")
	public @ResponseBody
	RestReturnMsg getRawDataTest(@PathVariable String sn) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		ZonedDateTime zdt;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		String sql = "select "
				+ "start_time as testStartTime, "
				+ "station, "
				+ "machine as testMachine, "
				+ "is_true_fail as isTrueFail, "
				+ "symptom as failSymptom "
				+ "from test "
				+ "where sn='" + sn + "' "
				+ "order by start_time";
		resultList = cockroachJtl.queryForList(sql);
		for (Map<String, Object> result : resultList) {
			try {
				if (result.get("testStartTime") != null) {
					result.replace("testStartTime", dateFormat.format(result.get("testStartTime")));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		HashMap dataMap = new HashMap<String, Object>();
		dataMap.put("sn", sn);
		dataMap.put("type", "test");
		dataMap.put("result", resultList);
		return new RestReturnMsg(200, "getRawData successfully", dataMap);
	}

	/**
	 * @param id
	 * @return
	 * @date 2019/5/26 下午14:12
	 * @description 取得 cockroach data
	 */
	@ApiOperation("取得 cockroach data detail")
	@RequiresAuthentication
	@GetMapping("/sn/{sn}/detail")
	public @ResponseBody
	RestReturnMsg getRawDataDetail(@PathVariable String sn) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		ZonedDateTime zdt;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		String sql = "select distinct "
				+ "b.scantime as assemblyTime, "
				+ "b.id, "
				+ "b.assembly_station as testStation, "
				+ "b.part, "
				+ "b.partsn, "
				+ "b.line "
				+ "from part_master a join part_detail b on a.id=b.id "
				+ "where sn='" + sn + "' "
				+ "order by b.scantime";
		resultList = cockroachJtl.queryForList(sql);
		for (Map<String, Object> result : resultList) {
			try {
				if (result.get("assemblyTime") != null) {
					result.replace("assemblyTime", dateFormat.format(result.get("assemblyTime")));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		HashMap dataMap = new HashMap<String, Object>();
		dataMap.put("sn", sn);
		dataMap.put("type", "detail");
		dataMap.put("result", resultList);
		return new RestReturnMsg(200, "getRawData successfully", dataMap);
	}

	/**
	 * @param id
	 * @return
	 * @date 2019/5/26 下午14:12
	 * @description 取得主動式驗證資料
	 */
	@ApiOperation("取得主動式驗證資料")
	@RequiresAuthentication
	@GetMapping("/summary")
	public @ResponseBody
	RestReturnMsg getInitiative() {
		String mysql = "";
		String cockroach = "";
		String minio = "";
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		String sql = "select max(start_time) as mysql from (select max(start_time) as start_time from risk_test_station_sn union all select max(start_time) as start_time from risk_test_station_sn_history) gg ";;
		resultList = mysqlJtl.queryForList(sql);
		if (resultList.size() > 0) {
			mysql = resultList.get(0).get("mysql") == null ? null : sdf.format(resultList.get(0).get("mysql"));
		}

		resultList = new ArrayList<Map<String, Object>>();
		sql = "select max(start_time) as cockroach from test ";
		resultList = cockroachJtl.queryForList(sql);
		if (resultList.size() > 0) {
			cockroach = resultList.get(0).get("cockroach") == null ? null : sdf.format(resultList.get(0).get("cockroach"));
		}

		resultList = new ArrayList<Map<String, Object>>();
		sql = "select max(minio_lastest_time) as minio from product_info ";
		resultList = mysqlJtl.queryForList(sql);
		if (resultList.size() > 0) {
			minio = resultList.get(0).get("minio") == null ? null : sdf.format(resultList.get(0).get("minio"));
		}

		HashMap dataMap = new HashMap<String, Object>();
		dataMap.put("mysql", mysql);
		dataMap.put("cockroach", cockroach);
		dataMap.put("minio", minio);
		return new RestReturnMsg(200, "getRawData successfully", dataMap);
	}

	/**
	 * @param id
	 * @return
	 * @date 2019/5/26 下午14:12
	 * @description 取得主動式驗證資料
	 */
	@ApiOperation("取得主動式驗證資料")
	@RequiresAuthentication
	@GetMapping("/summary/product")
	public @ResponseBody
	RestReturnMsg getInitiativeByProduct(@RequestParam("product") String product) {
		String mysql = "";
		String cockroach = "";
		String minio = "";
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		String sql = "select max(start_time) as mysql from (select max(start_time) as start_time from risk_test_station_sn union all select max(start_time) as start_time from risk_test_station_sn_history) gg ";;
		resultList = mysqlJtl.queryForList(sql);
		if (resultList.size() > 0) {
			mysql = resultList.get(0).get("mysql") == null ? null : sdf.format(resultList.get(0).get("mysql"));
		}

		resultList = new ArrayList<Map<String, Object>>();
		sql = "select max(a.start_time) as cockroach from test a join part_master b on a.sn = b.sn where b.product='" + product + "'";
		resultList = cockroachJtl.queryForList(sql);
		if (resultList.size() > 0) {
			cockroach = resultList.get(0).get("cockroach") == null ? null : sdf.format(resultList.get(0).get("cockroach"));
		}

		resultList = new ArrayList<Map<String, Object>>();
		sql = "select max(minio_lastest_time) as minio from product_info where product='" + product + "'";
		resultList = mysqlJtl.queryForList(sql);
		if (resultList.size() > 0) {
			minio = resultList.get(0).get("minio") == null ? null : sdf.format(resultList.get(0).get("minio"));
		}

		HashMap dataMap = new HashMap<String, Object>();
		dataMap.put("mysql", mysql);
		dataMap.put("cockroach", cockroach);
		dataMap.put("minio", minio);
		return new RestReturnMsg(200, "getRawData successfully", dataMap);
	}

}