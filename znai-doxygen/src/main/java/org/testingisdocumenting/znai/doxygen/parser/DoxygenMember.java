/*
 * Copyright 2021 znai maintainers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.testingisdocumenting.znai.doxygen.parser;

import org.testingisdocumenting.znai.extensions.api.ApiLinkedText;

import java.util.HashMap;
import java.util.Map;

public class DoxygenMember {
    private final DoxygenParameterList parameters;
    private final DoxygenParameterList templateParameters;

    private DoxygenCompound compound;
    private String id;
    private String name;
    private ApiLinkedText returnType;
    private DoxygenDescription description;
    private String visibility;
    private String kind;

    private String normalizedParamsSignature;

    private boolean isVirtual;
    private boolean isStatic;
    private boolean isConst;

    public DoxygenMember() {
        parameters = new DoxygenParameterList();
        templateParameters = new DoxygenParameterList();
    }

    public DoxygenDescription getDescription() {
        return description;
    }

    public void setDescription(DoxygenDescription description) {
        this.description = description;
    }

    public void addParameter(String name, ApiLinkedText type) {
        parameters.add(new DoxygenParameter(name, type));
    }

    public String getNormalizedParamsSignature() {
        return normalizedParamsSignature;
    }

    protected void buildNormalizedParamsSignature() {
        normalizedParamsSignature = parameters.generateCommaSeparatedTypes();
    }

    public void addTemplateParameter(String name, ApiLinkedText type) {
        if (type.contains("doc_ignore")) {
            return;
        }

        templateParameters.add(new DoxygenParameter(name, type));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DoxygenCompound getCompound() {
        return compound;
    }

    public void setCompound(DoxygenCompound compound) {
        this.compound = compound;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean matchesArgs(String normalizedArgsToMatch) {
        return normalizedParamsSignature.equals(normalizedArgsToMatch);
    }

    public String getFullName() {
        return DoxygenUtils.fullName(compound.getKind(), compound.getName(), name);
    }

    public ApiLinkedText getReturnType() {
        return returnType;
    }

    public void setReturnType(ApiLinkedText returnType) {
        this.returnType = returnType;
    }

    public DoxygenParameterList getParameters() {
        return parameters;
    }

    public boolean hasParameters() {
        return !parameters.isEmpty();
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public boolean isPublic() {
        return "public".equals(visibility);
    }

    public boolean isProtected() {
        return "protected".equals(visibility);
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public boolean isVariable() {
        return "variable".equals(kind);
    }

    public boolean isFunction() {
        return "function".equals(kind);
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public void setVirtual(boolean virtual) {
        isVirtual = virtual;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("compoundName", DoxygenUtils.compoundNameOrEmptyForFile(compound.getKind(), compound.getName()));
        result.put("compoundKind", compound.getKind());
        result.put("name", name);
        result.put("visibility", visibility);
        result.put("kind", kind);
        result.put("isVirtual", isVirtual);
        result.put("isFunction", isFunction());
        result.put("isConst", isConst);
        result.put("isStatic", isStatic);
        result.put("returnType", returnType.toListOfMaps());
        result.put("parameters", parameters.toListOfMaps());
        result.put("templateParameters", templateParameters.toListOfMaps());

        return result;
    }


}
