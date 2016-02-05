
/*
 * Copyright 2016 Gregory Sarrica and Kyle Chaplin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.autem;

/**
 * Created by gsarrica on 12/7/15.
 */
public class AutemConversation {

    private AutemContact autemContact;
    private AutemTextMessage autemTextMessage;

    public AutemContact getAutemContact() {
        return autemContact;
    }

    public void setAutemContact(AutemContact autemContact) {
        this.autemContact = autemContact;
    }

    public AutemTextMessage getAutemTextMessage() {
        return autemTextMessage;
    }

    public void setAutemTextMessage(AutemTextMessage autemTextMessage) {
        this.autemTextMessage = autemTextMessage;
    }

    @Override
    public String toString() {
        return "AutemConversation{" +
                "autemContact=" + autemContact +
                ", autemTextMessage=" + autemTextMessage +
                '}';
    }
}
