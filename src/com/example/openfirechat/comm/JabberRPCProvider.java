package com.example.openfirechat.comm;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.StringUtils;
import org.xmlpull.v1.XmlPullParser;

public class JabberRPCProvider implements IQProvider { 

    public IQ parseIQ(final XmlPullParser parser) throws Exception { 
            final StringBuffer buffer = new StringBuffer(); 

            // skip the <query> tag by calling parser.next() 
            while (true) { 
                    switch (parser.next()) { 
                    case XmlPullParser.TEXT: 
                            // We need to escape characters like & and < 
                            buffer.append(StringUtils.escapeForXML(parser.getText())); 
                            break; 

                    case XmlPullParser.START_TAG: 
                            buffer.append('<' + parser.getName() + '>'); 
                            break; 

                    case XmlPullParser.END_TAG: 
                            if ("query".equals(parser.getName())) { 
                                    // don't save the </query> end tag 
                                    return new JabberRPC(buffer.toString().trim()); 
                            } else { 
                                    buffer.append("</" + parser.getName() + '>'); 
                                    break; 
                            } 
                    default: 
                    } 
            } 
    } 

} 
