<%--
  ADOBE CONFIDENTIAL

  Copyright 2014 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page session="false"
          import="java.util.ArrayList,
                  java.util.Collections,
                  java.util.Comparator,
                  java.util.HashMap,
                  java.util.Iterator,
                  java.util.List,
                  java.text.Collator,
                  org.apache.commons.collections.Transformer,
                  org.apache.commons.collections.iterators.TransformIterator,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceMetadata,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.api.resource.ResourceUtil,
                  org.apache.sling.api.resource.ResourceWrapper,
                  org.apache.sling.api.resource.ValueMap,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.adobe.granite.ui.components.ds.EmptyDataSource,
                  com.adobe.granite.ui.components.ds.SimpleDataSource,
                  com.adobe.granite.ui.components.ds.ValueMapResource"%><%--###
Languages
=========

.. granite:servercomponent:: /libs/cq/gui/components/common/datasources/languages
   :datasource:
   
   A datasource providing languages key-value pairs.
   
   It is compatible with
   :granite:servercomponent:`Select </libs/granite/ui/components/foundation/form/select>` and :granite:servercomponent:`Autocomplete </libs/granite/ui/components/foundation/form/autocomplete>`.
   
   It has the following content structure:

   .. gnd:gnd::

      [cq:CommonDataSourcesLanguages]
      
      /**
       * ``true`` to add "empty" language; ``false`` otherwise.
       */
      - addNone (Boolean)
###--%><%
    final ResourceResolver resolver = resourceResolver;

    Config dsCfg = new Config(resource.getChild("datasource"));
    Resource langRes = resolver.getResource("wcm/core/resources/languages");
    
    if (ResourceUtil.isNonExistingResource(langRes)) {
        request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
        return;
    }

    List<KeyValue> languages = new ArrayList<KeyValue>();
    
    if (dsCfg.get("addNone", false)) {
        languages.add(new KeyValue("", ""));
    }

    for (Iterator<Resource> it = langRes.listChildren(); it.hasNext();) {
        Resource lang = it.next();
        ValueMap vm = ResourceUtil.getValueMap(lang);
        
        String country = i18n.getVar(vm.get("country", lang.getName()));
        
        String value = i18n.getVar(vm.get("language", lang.getName()));
        if (!country.equals("*")) {
            value += " (" + country + ")";
        }

        if(lang.getName().length() == 2) {
        	languages.add(new KeyValue(lang.getName(), value));
        }
    }
    
    final Collator langCollator = Collator.getInstance(request.getLocale());

    Collections.sort(languages, new Comparator<KeyValue>() {
        public int compare(KeyValue o1, KeyValue o2) {
            return langCollator.compare(o1.value, o2.value);
        }
    });

    @SuppressWarnings("unchecked")
    DataSource ds = new SimpleDataSource(new TransformIterator(languages.iterator(), new Transformer() {
        public Object transform(Object input) {
            try {
                KeyValue lang = (KeyValue) input;

                ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
                vm.put("value", lang.key);
                vm.put("text", lang.value);

                return new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }));
    
    request.setAttribute(DataSource.class.getName(), ds);
%><%!
    private class KeyValue {
        String key;
        String value;
        
        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
%>