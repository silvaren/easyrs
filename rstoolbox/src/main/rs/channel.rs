#pragma version(1)
#pragma rs java_package_name(silvaren.rstoolbox.scripts)

uchar __attribute__((kernel)) channelR(uchar4 in, uint32_t x, uint32_t y) {
  uchar out = in.r;
  return out;
}