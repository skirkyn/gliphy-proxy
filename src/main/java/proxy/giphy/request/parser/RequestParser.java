package proxy.giphy.request.parser;

import proxy.giphy.types.Request;

public interface RequestParser {
    Request parse(String firstLine);
}
