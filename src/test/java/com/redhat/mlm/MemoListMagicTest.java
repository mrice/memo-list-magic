package com.redhat.mlm;

import org.junit.Test;

import javax.mail.Address;
import javax.mail.Message;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MemoListMagicTest {

    @Test
    public void testMessageFilter() throws Exception {

        Message memoListMessage = mock(Message.class);
        Message notAMemoListMessage = mock(Message.class);
        Message anotherMemoListMessage = mock(Message.class);

        Address memoListAddress = mock(Address.class);
        when(memoListAddress.toString()).thenReturn("memo-list@redhat.com");
        Address michaelsAddress = mock(Address.class);
        when(michaelsAddress.toString()).thenReturn("mrice@redhat.com");

        when(memoListMessage.getReplyTo()).thenReturn(multAddrBuilder(memoListAddress));
        when(notAMemoListMessage.getReplyTo()).thenReturn(multAddrBuilder(michaelsAddress));
        when(anotherMemoListMessage.getReplyTo()).thenReturn(multAddrBuilder(michaelsAddress, memoListAddress));

        MemoListMagic memoListMagic = new MemoListMagic();

        Message unfilteredMessages[] = {memoListMessage, notAMemoListMessage, anotherMemoListMessage};

        List<Message> filteredMessages = memoListMagic.filterMemoListMessagesToList(unfilteredMessages);
        assertNotNull(filteredMessages);
        assertEquals(2, filteredMessages.size());

    }

    @Test
    public void testAddressesContainMemoList() {

        Address memoListAddress = mock(Address.class);
        when(memoListAddress.toString()).thenReturn("memo-list@redhat.com");
        Address michaelsAddress = mock(Address.class);
        when(michaelsAddress.toString()).thenReturn("mrice@redhat.com");

        MemoListMagic memoListMagic = new MemoListMagic();
        assertFalse(memoListMagic.addressesContainMemoList(multAddrBuilder(michaelsAddress)));
        assertTrue(memoListMagic.addressesContainMemoList(multAddrBuilder(memoListAddress)));
        assertTrue(memoListMagic.addressesContainMemoList(multAddrBuilder(michaelsAddress, memoListAddress)));

    }


    @Test
    public void testNormalizeSubjectLine() throws Exception {

        Message oneMessage = mock(Message.class);
        Message replyMessage = mock(Message.class);

        String whinyMemoListSubjectLne = "The coffee Maker on 16 is the WORST!";
        when(oneMessage.getSubject()).thenReturn(whinyMemoListSubjectLne);
        when(replyMessage.getSubject()).thenReturn("RE: "+whinyMemoListSubjectLne);

        MemoListMagic memoListMagic = new MemoListMagic();
        assertEquals(whinyMemoListSubjectLne, memoListMagic.normalizeSubjectLine(oneMessage));
        assertEquals(whinyMemoListSubjectLne, memoListMagic.normalizeSubjectLine(replyMessage));

    }

    //handy way to convert to an array and still be readable, right? Or lame?
    Address[] multAddrBuilder(Address... addresses) {
        return addresses;
    }

}
