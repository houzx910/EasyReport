package org.easyframework.report.engine;

import java.util.List;

import org.easyframework.report.engine.data.HorizontalStatColumnDataSet;
import org.easyframework.report.engine.data.LayoutType;
import org.easyframework.report.engine.data.ReportDataSet;
import org.easyframework.report.engine.data.ReportDataSource;
import org.easyframework.report.engine.data.ReportMetaDataColumn;
import org.easyframework.report.engine.data.ReportMetaDataRow;
import org.easyframework.report.engine.data.ReportMetaDataSet;
import org.easyframework.report.engine.data.ReportParameter;
import org.easyframework.report.engine.data.VerticalStatColumnDataSet;
import org.easyframework.report.engine.query.Queryer;
import org.easyframework.report.engine.query.QueryerFactory;

/**
 *
 * 数据执行器类，负责选择正确的报表查询器并获取数据，最终转化为成报表的数据集
 *
 */
public class DataExecutor {
	private final ReportParameter parameter;
	private final ReportDataSource dataSource;
	private final Queryer queryer;

	/**
	 * 数据执行器
	 * 
	 * @param parameter
	 *            报表参数对象
	 */
	public DataExecutor(ReportParameter parameter) {
		this.parameter = parameter;
		this.dataSource = null;
		this.queryer = null;
	}

	/**
	 * 数据执行器
	 * 
	 * @param dataSource
	 *            报表数据源配置对象
	 * @param parameter
	 *            报表参数对象
	 */
	public DataExecutor(ReportDataSource dataSource, ReportParameter parameter) {
		this.dataSource = dataSource;
		this.parameter = parameter;
		this.queryer = null;
	}

	/**
	 * 数据执行器
	 * 
	 * @param queryer
	 *            报表查询器对象
	 * @param parameter
	 *            报表参数对象
	 */
	public DataExecutor(Queryer queryer, ReportParameter parameter) {
		this.dataSource = null;
		this.parameter = parameter;
		this.queryer = queryer;
	}

	/**
	 * 
	 * 选择正确的报表查询器并获取数据，最终转化为成报表的数据集
	 * 
	 * @return ReportDataSet报表数据集对象
	 */
	public ReportDataSet execute() {
		Queryer queryer = this.getQueryer();
		if (queryer == null) {
			throw new RuntimeException("未指定报表查询器对象!");
		}
		List<ReportMetaDataColumn> metaDataColumns = queryer.getMetaDataColumns();
		List<ReportMetaDataRow> metaDataRows = queryer.getMetaDataRows();
		ReportMetaDataSet metaDataSet = new ReportMetaDataSet(metaDataRows, metaDataColumns, this.parameter.getEnabledStatColumns());
		return this.parameter.getStatColumnLayout() == LayoutType.VERTICAL ?
				new VerticalStatColumnDataSet(metaDataSet, this.parameter.getLayout(), this.parameter.getStatColumnLayout()) :
				new HorizontalStatColumnDataSet(metaDataSet, this.parameter.getLayout(), this.parameter.getStatColumnLayout());
	}

	/**
	 * 选择正确的报表查询器并获取数据，最终转化为成报表的数据集
	 * 
	 * @param metaDataSet
	 * @return ReportDataSet报表数据集对象
	 */
	public ReportDataSet execute(ReportMetaDataSet metaDataSet) {
		if (metaDataSet == null) {
			throw new RuntimeException("报表元数据集不能为null!");
		}
		return this.parameter.getStatColumnLayout() == LayoutType.VERTICAL ?
				new VerticalStatColumnDataSet(metaDataSet, this.parameter.getLayout(), this.parameter.getStatColumnLayout()) :
				new HorizontalStatColumnDataSet(metaDataSet, this.parameter.getLayout(), this.parameter.getStatColumnLayout());
	}

	private Queryer getQueryer() {
		if (this.queryer != null) {
			return this.queryer;
		}
		return QueryerFactory.create(this.dataSource, this.parameter);
	}
}
