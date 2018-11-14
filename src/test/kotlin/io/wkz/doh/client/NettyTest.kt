package io.wkz.doh.client

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.*
import org.junit.Test
import java.net.URI




/**
 *
 * @author 王可尊
 * @since 1.0
 */
class NettyTest {
	@Test
	fun test(){
		val workerGroup = NioEventLoopGroup()

		try {
			val b = Bootstrap()
			b.group(workerGroup)
			b.channel(NioSocketChannel::class.java)
			b.option(ChannelOption.SO_KEEPALIVE, true)
			b.handler(object : ChannelInitializer<SocketChannel>() {
				@Throws(Exception::class)
				public override fun initChannel(ch: SocketChannel) {
					// 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
					ch.pipeline().addLast(HttpResponseDecoder())
					// 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
					ch.pipeline().addLast(HttpRequestEncoder())
					ch.pipeline().addLast(HttpClientInboundHandler())
				}
			})

			// Start the client.
			val f = b.connect("61.135.169.121", 80).sync()

			val uri = URI("http://baidu.com")
			val msg = "Are you ok?"
			val request = DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
												 HttpMethod.GET,
												 uri.toASCIIString(),
												 Unpooled.wrappedBuffer(msg.toByteArray(charset("UTF-8"))))

			// 构建http请求
			request.headers().set(HttpHeaders.Names.HOST, "61.135.169.121")
			request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
			request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes())
			// 发送http请求
			f.channel().write(request)
			f.channel().flush()
			f.channel().closeFuture().sync()
		} finally {
			workerGroup.shutdownGracefully()
		}

	}
}

class HttpClientInboundHandler: ChannelInboundHandlerAdapter() {
	@Throws(Exception::class)
	override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
		if (msg is HttpResponse) {
			System.out.println("CONTENT_TYPE:" + msg.headers().get(HttpHeaders.Names.CONTENT_TYPE))
		}
		if (msg is HttpContent) {
			val buf = msg.content()
			println(buf.toString(io.netty.util.CharsetUtil.UTF_8))
			buf.release()
		}
	}
}
