/**
 *    Copyright 2017 ICF Olson
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.citytechinc.cq.component.dialog.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import org.codehaus.plexus.util.StringUtils;

import com.citytechinc.cq.component.dialog.DialogElement;
import com.citytechinc.cq.component.dialog.exception.InvalidComponentFieldException;
import com.citytechinc.cq.component.dialog.maker.WidgetMaker;
import com.citytechinc.cq.component.dialog.maker.WidgetMakerParameters;
import com.citytechinc.cq.component.dialog.maker.impl.DefaultWidgetMaker;
import com.citytechinc.cq.component.maven.util.LogSingleton;
import com.citytechinc.cq.component.util.WidgetConfigHolder;

public class WidgetFactory {

	public static final String TEXTFIELD_XTYPE = "textfield";
	public static final String NUMBERFIELD_XTYPE = "numberfield";
	public static final String PATHFIELD_XTYPE = "pathfield";
	public static final String SELECTION_XTYPE = "selection";
	public static final String MULTIFIELD_XTYPE = "multifield";
	public static final String HTML5SMARTIMAGE_XTYPE = "html5smartimage";

	private WidgetFactory() {
	}

	public static DialogElement make(WidgetMakerParameters parameters, int rankingCeiling)
		throws InvalidComponentFieldException, ClassNotFoundException, CannotCompileException, NotFoundException,
		SecurityException, NoSuchFieldException, InstantiationException, IllegalAccessException,
		IllegalArgumentException, InvocationTargetException, NoSuchMethodException {

		WidgetMakerContext widgetMakerContext = getWidgetMakerForField(parameters, rankingCeiling);

		if (widgetMakerContext != null) {
			parameters.setXtype(widgetMakerContext.getXtype());
			WidgetMaker widgetMaker =
				widgetMakerContext.getWidgetMaker().getConstructor(WidgetMakerParameters.class).newInstance(parameters);

			return widgetMaker.make();
		}

		return null;
	}

	private static final WidgetMakerContext
		getWidgetMakerForField(WidgetMakerParameters parameters, int rankingCeiling) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, InvalidComponentFieldException {

		WidgetConfigHolder widget = getWidgetConfig(parameters, rankingCeiling);

		if (widget != null && widget.hasMakerClass()) {
			return new WidgetMakerContext(widget.getMakerClass(), widget.getXtype());
		}

		if (widget != null && widget.hasXtype()) {
			return new WidgetMakerContext(DefaultWidgetMaker.class, widget.getXtype());
		}

		String xtype = getXTypeForField(parameters);

		if (StringUtils.isNotEmpty(xtype)) {
			widget = getWidgetConfigByXtype(xtype, parameters, rankingCeiling);
			if (widget != null && widget.hasMakerClass()) {
				return new WidgetMakerContext(widget.getMakerClass(), widget.getXtype());
			}
			return new WidgetMakerContext(DefaultWidgetMaker.class, xtype);
		}
		return null;
	}

	private static final String getXTypeForField(WidgetMakerParameters parameters)
		throws InvalidComponentFieldException {

		if (StringUtils.isNotEmpty(parameters.getDialogFieldConfig().getXtype())) {
			return parameters.getDialogFieldConfig().getXtype();
		}

		return null;
	}

	public static WidgetConfigHolder getWidgetConfig(WidgetMakerParameters parameters, int rankCeiling)
		throws ClassNotFoundException {

		LogSingleton LOG = LogSingleton.getInstance();

		WidgetConfigHolder highestRankedWidget = null;

		Set<Class<?>> registeredAnnotations = parameters.getWidgetRegistry().getRegisteredAnnotations();

		for (Class<?> curRegisteredAnnotation : registeredAnnotations) {
			LOG.debug("Checking for known annotation " + curRegisteredAnnotation);
			if (parameters.getCtMember().hasAnnotation(curRegisteredAnnotation)) {
				WidgetConfigHolder curPotential =
					parameters.getWidgetRegistry().getWidgetForAnnotation(curRegisteredAnnotation);
				if (rankCeiling < 0 || curPotential.getRanking() < rankCeiling) {
					LOG.debug("Match found in the registry with ranking " + curPotential.getRanking());
					if (highestRankedWidget == null || curPotential.getRanking() > highestRankedWidget.getRanking()) {
						highestRankedWidget = curPotential;
					}
				}
			}
		}

		if (highestRankedWidget != null) {
			return highestRankedWidget;
		}

		return null;

	}

	public static WidgetConfigHolder getWidgetConfigByXtype(String xtype, WidgetMakerParameters parameters,
		int rankCeiling) {
		LogSingleton LOG = LogSingleton.getInstance();

		WidgetConfigHolder highestRankedWidget = null;

		Set<Class<?>> registeredAnnotations = parameters.getWidgetRegistry().getRegisteredAnnotations();

		for (Class<?> curRegisteredAnnotation : registeredAnnotations) {
			LOG.debug("Checking for known annotation " + curRegisteredAnnotation);
			WidgetConfigHolder curPotential =
				parameters.getWidgetRegistry().getWidgetForAnnotation(curRegisteredAnnotation);
			if (curPotential.getXtype().equals(xtype) && rankCeiling < 0 || curPotential.getRanking() < rankCeiling) {
				LOG.debug("Match found in the registry with ranking " + curPotential.getRanking());
				if (highestRankedWidget == null || curPotential.getRanking() > highestRankedWidget.getRanking()) {
					highestRankedWidget = curPotential;
				}
			}

		}

		if (highestRankedWidget != null) {
			return highestRankedWidget;
		}

		return null;
	}

}
