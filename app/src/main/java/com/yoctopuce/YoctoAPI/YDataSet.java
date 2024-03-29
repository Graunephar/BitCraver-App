/*********************************************************************
 * $Id: YDataSet.java 28024 2017-07-10 08:50:02Z mvuilleu $
 *
 * Implements yFindDataSet(), the high-level API for DataSet functions
 *
 * - - - - - - - - - License information: - - - - - - - - -
 *
 * Copyright (C) 2011 and beyond by Yoctopuce Sarl, Switzerland.
 *
 * Yoctopuce Sarl (hereafter Licensor) grants to you a perpetual
 * non-exclusive license to use, modify, copy and integrate this
 * file into your software for the sole purpose of interfacing
 * with Yoctopuce products.
 *
 * You may reproduce and distribute copies of this file in
 * source or object form, as long as the sole purpose of this
 * code is to interface with Yoctopuce products. You must retain
 * this notice in the distributed source file.
 *
 * You should refer to Yoctopuce General Terms and Conditions
 * for additional information regarding your rights and
 * obligations.
 *
 * THE SOFTWARE AND DOCUMENTATION ARE PROVIDED 'AS IS' WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING
 * WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, TITLE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL LICENSOR BE LIABLE FOR ANY INCIDENTAL, SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA,
 * COST OF PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY OR
 * SERVICES, ANY CLAIMS BY THIRD PARTIES (INCLUDING BUT NOT
 * LIMITED TO ANY DEFENSE THEREOF), ANY CLAIMS FOR INDEMNITY OR
 * CONTRIBUTION, OR OTHER SIMILAR COSTS, WHETHER ASSERTED ON THE
 * BASIS OF CONTRACT, TORT (INCLUDING NEGLIGENCE), BREACH OF
 * WARRANTY, OR OTHERWISE.
 *********************************************************************/

package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

//--- (generated code: YDataSet class start)
/**
 * YDataSet Class: Recorded data sequence
 *
 * YDataSet objects make it possible to retrieve a set of recorded measures
 * for a given sensor and a specified time interval. They can be used
 * to load data points with a progress report. When the YDataSet object is
 * instantiated by the get_recordedData()  function, no data is
 * yet loaded from the module. It is only when the loadMore()
 * method is called over and over than data will be effectively loaded
 * from the dataLogger.
 *
 * A preview of available measures is available using the function
 * get_preview() as soon as loadMore() has been called
 * once. Measures themselves are available using function get_measures()
 * when loaded by subsequent calls to loadMore().
 *
 * This class can only be used on devices that use a recent firmware,
 * as YDataSet objects are not supported by firmwares older than version 13000.
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedAssignment"})
public class YDataSet
{
//--- (end of generated code: YDataSet class start)

    //--- (generated code: YDataSet definitions)
    protected YFunction _parent;
    protected String _hardwareId;
    protected String _functionId;
    protected String _unit;
    protected long _startTime = 0;
    protected long _endTime = 0;
    protected int _progress = 0;
    protected ArrayList<Integer> _calib = new ArrayList<>();
    protected ArrayList<YDataStream> _streams = new ArrayList<>();
    protected YMeasure _summary;
    protected ArrayList<YMeasure> _preview = new ArrayList<>();
    protected ArrayList<YMeasure> _measures = new ArrayList<>();

    //--- (end of generated code: YDataSet definitions)

    // YDataSet constructor, when instantiated directly by a function
    public YDataSet(YFunction parent, String functionId, String unit, long startTime, long endTime)
    {
        _parent = parent;
        _functionId = functionId;
        _unit = unit;
        _startTime = startTime;
        _endTime = endTime;
        _progress = -1;
        _hardwareId = "";
        _summary = new YMeasure();
    }

    // YDataSet constructor for the new datalogger
    public YDataSet(YFunction parent)
    {
        _parent = parent;
        _startTime = 0;
        _endTime = 0;
        _hardwareId = "";
        _summary = new YMeasure();
    }

    // YDataSet parser for stream list
    protected int _parse(String json_str) throws YAPI_Exception
    {
        YJSONObject json;
        YJSONArray jstreams;
        double summaryMinVal = Double.MAX_VALUE;
        double summaryMaxVal = Double.MIN_VALUE;
        double summaryTotalTime = 0;
        double summaryTotalAvg = 0;
        long streamStartTime;
        long streamEndTime;
        long startTime = 0x7fffffff;
        long endTime = 0;

        try {
            json = new YJSONObject(json_str);
            json.parse();
            _functionId = json.getString("id");
            _unit = json.getString("unit");
            if (json.has("calib")) {
                _calib = YAPIContext._decodeFloats(json.getString("calib"));
                _calib.set(0, _calib.get(0) / 1000);
            } else {
                _calib = YAPIContext._decodeWords(json.getString("cal"));
            }
            _streams = new ArrayList<>();
            _preview = new ArrayList<>();
            _measures = new ArrayList<>();
            jstreams = json.getYJSONArray("streams");
            for (int i = 0; i < jstreams.length(); i++) {
                YDataStream stream = _parent._findDataStream(this, jstreams.getString(i));
                streamStartTime = stream.get_startTimeUTC() - stream.get_dataSamplesIntervalMs() / 1000;
                streamEndTime = stream.get_startTimeUTC() + stream.get_duration();
                if (_startTime > 0 && streamEndTime <= _startTime) {
                    // this stream is too early, drop it
                } else if (_endTime > 0 && stream.get_startTimeUTC() > _endTime) {
                    // this stream is too late, drop it
                } else {
                    _streams.add(stream);
                    if (startTime > streamStartTime) {
                        startTime = streamStartTime;
                    }
                    if (endTime < streamEndTime) {
                        endTime = streamEndTime;
                    }

                    if (stream.isClosed() && stream.get_startTimeUTC() >= _startTime &&
                            (_endTime == 0 || streamEndTime <= _endTime)) {
                        if (summaryMinVal > stream.get_minValue())
                            summaryMinVal = stream.get_minValue();
                        if (summaryMaxVal < stream.get_maxValue())
                            summaryMaxVal = stream.get_maxValue();
                        summaryTotalAvg += stream.get_averageValue() * stream.get_duration();
                        summaryTotalTime += stream.get_duration();

                        YMeasure rec = new YMeasure(stream.get_startTimeUTC(),
                                streamEndTime,
                                stream.get_minValue(),
                                stream.get_averageValue(),
                                stream.get_maxValue());
                        _preview.add(rec);
                    }
                }
            }
            if ((_streams.size() > 0) && (summaryTotalTime > 0)) {
                // update time boundaries with actual data
                if (_startTime < startTime) {
                    _startTime = startTime;
                }
                if (_endTime == 0 || _endTime > endTime) {
                    _endTime = endTime;
                }
                _summary = new YMeasure(_startTime, _endTime, summaryMinVal, summaryTotalAvg / summaryTotalTime, summaryMaxVal);
            }
        } catch (Exception e) {
            throw new YAPI_Exception(YAPI.IO_ERROR, "invalid json structure for YDataSet: " + e.getMessage());
        }
        _progress = 0;
        return this.get_progress();
    }

    //--- (generated code: YDataSet implementation)

    public ArrayList<Integer> _get_calibration()
    {
        return _calib;
    }

    public int processMore(int progress,byte[] data) throws YAPI_Exception
    {
        YDataStream stream;
        ArrayList<ArrayList<Double>> dataRows = new ArrayList<>();
        String strdata;
        double tim;
        double itv;
        int nCols;
        int minCol;
        int avgCol;
        int maxCol;

        if (progress != _progress) {
            return _progress;
        }
        if (_progress < 0) {
            strdata = new String(data);
            if (strdata.equals("{}")) {
                _parent._throw(YAPI.VERSION_MISMATCH, "device firmware is too old");
                return YAPI.VERSION_MISMATCH;
            }
            return _parse(strdata);
        }
        stream = _streams.get(_progress);
        stream._parseStream(data);
        dataRows = stream.get_dataRows();
        _progress = _progress + 1;
        if (dataRows.size() == 0) {
            return get_progress();
        }
        tim = (double) stream.get_startTimeUTC();
        itv = stream.get_dataSamplesInterval();
        if (tim < itv) {
            tim = itv;
        }
        nCols = dataRows.get(0).size();
        minCol = 0;
        if (nCols > 2) {
            avgCol = 1;
        } else {
            avgCol = 0;
        }
        if (nCols > 2) {
            maxCol = 2;
        } else {
            maxCol = 0;
        }

        for (ArrayList<Double> ii:dataRows) {
            if ((tim >= _startTime) && ((_endTime == 0) || (tim <= _endTime))) {
                _measures.add(new YMeasure(tim - itv, tim, ii.get(minCol).doubleValue(), ii.get(avgCol).doubleValue(), ii.get(maxCol).doubleValue()));
            }
            tim = tim + itv;
            tim = (double)Math.round(tim * 1000) / 1000.0;
        }
        return get_progress();
    }

    public ArrayList<YDataStream> get_privateDataStreams()
    {
        return _streams;
    }

    /**
     * Returns the unique hardware identifier of the function who performed the measures,
     * in the form SERIAL.FUNCTIONID. The unique hardware identifier is composed of the
     * device serial number and of the hardware identifier of the function
     * (for example THRMCPL1-123456.temperature1)
     *
     * @return a string that uniquely identifies the function (ex: THRMCPL1-123456.temperature1)
     *
     * @throws YAPI_Exception on error
     */
    public String get_hardwareId() throws YAPI_Exception
    {
        YModule mo;
        if (!(_hardwareId.equals(""))) {
            return _hardwareId;
        }
        mo = _parent.get_module();
        _hardwareId = String.format(Locale.US, "%s.%s", mo.get_serialNumber(),get_functionId());
        return _hardwareId;
    }

    /**
     * Returns the hardware identifier of the function that performed the measure,
     * without reference to the module. For example temperature1.
     *
     * @return a string that identifies the function (ex: temperature1)
     */
    public String get_functionId()
    {
        return _functionId;
    }

    /**
     * Returns the measuring unit for the measured value.
     *
     * @return a string that represents a physical unit.
     *
     * @throws YAPI_Exception on error
     */
    public String get_unit() throws YAPI_Exception
    {
        return _unit;
    }

    /**
     * Returns the start time of the dataset, relative to the Jan 1, 1970.
     * When the YDataSet is created, the start time is the value passed
     * in parameter to the get_dataSet() function. After the
     * very first call to loadMore(), the start time is updated
     * to reflect the timestamp of the first measure actually found in the
     * dataLogger within the specified range.
     *
     * @return an unsigned number corresponding to the number of seconds
     *         between the Jan 1, 1970 and the beginning of this data
     *         set (i.e. Unix time representation of the absolute time).
     */
    public long get_startTimeUTC()
    {
        return _startTime;
    }

    /**
     * Returns the end time of the dataset, relative to the Jan 1, 1970.
     * When the YDataSet is created, the end time is the value passed
     * in parameter to the get_dataSet() function. After the
     * very first call to loadMore(), the end time is updated
     * to reflect the timestamp of the last measure actually found in the
     * dataLogger within the specified range.
     *
     * @return an unsigned number corresponding to the number of seconds
     *         between the Jan 1, 1970 and the end of this data
     *         set (i.e. Unix time representation of the absolute time).
     */
    public long get_endTimeUTC()
    {
        return _endTime;
    }

    /**
     * Returns the progress of the downloads of the measures from the data logger,
     * on a scale from 0 to 100. When the object is instantiated by get_dataSet,
     * the progress is zero. Each time loadMore() is invoked, the progress
     * is updated, to reach the value 100 only once all measures have been loaded.
     *
     * @return an integer in the range 0 to 100 (percentage of completion).
     */
    public int get_progress()
    {
        if (_progress < 0) {
            return 0;
        }
        // index not yet loaded
        if (_progress >= _streams.size()) {
            return 100;
        }
        return ((1 + (1 + _progress) * 98) / ((1 + _streams.size())));
    }

    /**
     * Loads the the next block of measures from the dataLogger, and updates
     * the progress indicator.
     *
     * @return an integer in the range 0 to 100 (percentage of completion),
     *         or a negative error code in case of failure.
     *
     * @throws YAPI_Exception on error
     */
    public int loadMore() throws YAPI_Exception
    {
        String url;
        YDataStream stream;
        if (_progress < 0) {
            url = String.format(Locale.US, "logger.json?id=%s",_functionId);
            if (_startTime != 0) {
                url = String.format(Locale.US, "%s&from=%d",url,_startTime);
            }
            if (_endTime != 0) {
                url = String.format(Locale.US, "%s&to=%d",url,_endTime);
            }
        } else {
            if (_progress >= _streams.size()) {
                return 100;
            } else {
                stream = _streams.get(_progress);
                url = stream._get_url();
            }
        }
        return processMore(_progress, _parent._download(url));
    }

    /**
     * Returns an YMeasure object which summarizes the whole
     * DataSet. In includes the following information:
     * - the start of a time interval
     * - the end of a time interval
     * - the minimal value observed during the time interval
     * - the average value observed during the time interval
     * - the maximal value observed during the time interval
     *
     * This summary is available as soon as loadMore() has
     * been called for the first time.
     *
     * @return an YMeasure object
     */
    public YMeasure get_summary()
    {
        return _summary;
    }

    /**
     * Returns a condensed version of the measures that can
     * retrieved in this YDataSet, as a list of YMeasure
     * objects. Each item includes:
     * - the start of a time interval
     * - the end of a time interval
     * - the minimal value observed during the time interval
     * - the average value observed during the time interval
     * - the maximal value observed during the time interval
     *
     * This preview is available as soon as loadMore() has
     * been called for the first time.
     *
     * @return a table of records, where each record depicts the
     *         measured values during a time interval
     *
     * @throws YAPI_Exception on error
     */
    public ArrayList<YMeasure> get_preview() throws YAPI_Exception
    {
        return _preview;
    }

    /**
     * Returns the detailed set of measures for the time interval corresponding
     * to a given condensed measures previously returned by get_preview().
     * The result is provided as a list of YMeasure objects.
     *
     * @param measure : condensed measure from the list previously returned by
     *         get_preview().
     *
     * @return a table of records, where each record depicts the
     *         measured values during a time interval
     *
     * @throws YAPI_Exception on error
     */
    public ArrayList<YMeasure> get_measuresAt(YMeasure measure) throws YAPI_Exception
    {
        long startUtc;
        YDataStream stream;
        ArrayList<ArrayList<Double>> dataRows = new ArrayList<>();
        ArrayList<YMeasure> measures = new ArrayList<>();
        double tim;
        double itv;
        int nCols;
        int minCol;
        int avgCol;
        int maxCol;

        startUtc = (long) (double)Math.round(measure.get_startTimeUTC());
        stream = null;
        for (YDataStream ii:_streams) {
            if (ii.get_startTimeUTC() == startUtc) {
                stream = ii;
            }
        }
        if (stream == null) {
            return measures;
        }
        dataRows = stream.get_dataRows();
        if (dataRows.size() == 0) {
            return measures;
        }
        tim = (double) stream.get_startTimeUTC();
        itv = stream.get_dataSamplesInterval();
        if (tim < itv) {
            tim = itv;
        }
        nCols = dataRows.get(0).size();
        minCol = 0;
        if (nCols > 2) {
            avgCol = 1;
        } else {
            avgCol = 0;
        }
        if (nCols > 2) {
            maxCol = 2;
        } else {
            maxCol = 0;
        }

        for (ArrayList<Double> ii:dataRows) {
            if ((tim >= _startTime) && ((_endTime == 0) || (tim <= _endTime))) {
                measures.add(new YMeasure(tim - itv, tim, ii.get(minCol).doubleValue(), ii.get(avgCol).doubleValue(), ii.get(maxCol).doubleValue()));
            }
            tim = tim + itv;
        }
        return measures;
    }

    /**
     * Returns all measured values currently available for this DataSet,
     * as a list of YMeasure objects. Each item includes:
     * - the start of the measure time interval
     * - the end of the measure time interval
     * - the minimal value observed during the time interval
     * - the average value observed during the time interval
     * - the maximal value observed during the time interval
     *
     * Before calling this method, you should call loadMore()
     * to load data from the device. You may have to call loadMore()
     * several time until all rows are loaded, but you can start
     * looking at available data rows before the load is complete.
     *
     * The oldest measures are always loaded first, and the most
     * recent measures will be loaded last. As a result, timestamps
     * are normally sorted in ascending order within the measure table,
     * unless there was an unexpected adjustment of the datalogger UTC
     * clock.
     *
     * @return a table of records, where each record depicts the
     *         measured value for a given time interval
     *
     * @throws YAPI_Exception on error
     */
    public ArrayList<YMeasure> get_measures() throws YAPI_Exception
    {
        return _measures;
    }

    //--- (end of generated code: YDataSet implementation)
}

